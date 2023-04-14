package learner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import de.learnlib.acex.analyzers.AcexAnalyzers;
import de.learnlib.algorithms.ttt.mealy.TTTLearnerMealy;
import de.learnlib.api.algorithm.LearningAlgorithm.MealyLearner;
import de.learnlib.api.oracle.EquivalenceOracle.MealyEquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
import de.learnlib.api.query.DefaultQuery;
import de.learnlib.oracle.equivalence.MealyEQOracleChain;
import de.learnlib.oracle.equivalence.MealyRandomWpMethodEQOracle;
import de.learnlib.oracle.equivalence.MealyWpMethodEQOracle;
import learner.Config.EquType;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.MutableMealyMachine;
import net.automatalib.automata.transducers.impl.FastMealy;
import net.automatalib.serialization.dot.GraphDOT;
import net.automatalib.util.automata.Automata;
import net.automatalib.util.automata.builders.AutomatonBuilders;
import net.automatalib.util.automata.copy.AutomatonCopyMethod;
import net.automatalib.util.automata.copy.AutomatonLowLevelCopy;
import net.automatalib.words.Word;
import net.automatalib.words.impl.ListAlphabet;
import util.FileManager;
import util.SoundUtils;

public class Main {

	private static final String EMPTY_DATABASE = "/querylog-empty.db";

	private PrintStream statisticsFileStream;

	public static void main(String[] args) {
		Main main = new Main();
		try {
			if (args.length == 0) {
				System.err.println("A configuration file with learning parameters is required.");
				System.err.println("See '${ssh-learner}/input/config.prop' for an example including all the configurable parameters.");
				return;
			}
			String configFile = args[0];
			final Config config = new Config(configFile, Arrays.copyOfRange(args, 1, args.length));
			MealyMachine<?, String, ?, String>  learnedModel = main.learn(config);
			SoundUtils.success();
			File modelFile = new File(config.outputFolder + "learnedModel.dot");
			// Write final dot
			GraphDOT.write(learnedModel, config.alphabet,  new FileWriter(modelFile));
			FileManager.copy(configFile, config.outputFolder + "config.prop");

		} catch (Exception e) {
			e.printStackTrace();
			SoundUtils.failure();
		}
	}

	public Main() {
	}

	public MealyMachine<?, String, ?, String>  learn(Config config) throws Exception {
		String expFolder = config.outputFolder;
		if (!new File(expFolder).exists()) {
			new File(expFolder).mkdirs();
		}
		
		String statisticsFile = expFolder + "statistics.txt";
		String dbFile = expFolder + "cache.db";
		
		// All output goes to file
		statisticsFileStream = new PrintStream(new FileOutputStream(statisticsFile, false));
		statisticsFileStream.println("Effective Configuration: " + config.getEffectiveConfigString());
		
		// Set up a connection
		Socket sock = new Socket(config.mapperHost, config.mapperPort);
		System.out.println("Created a shared socket on local port " + sock.getLocalPort());

		// Disable Nagle's delay algorithm
		sock.setTcpNoDelay(true);

		// Logging info goes to System.out
//		LearnLogger.addAppender(
//				new PrintStreamLoggingAppender(LogLevel.DEBUG, new PrintStream(new FileOutputStream(learnLog, false))));

		// Some debug info
		System.out.println("Started the main process");
		System.out.println("Maximum number of traces: " + config.maxNumTests);
		System.out.println("Effective Configuration: " + config.getEffectiveConfigString());

		// if cache not present, make a copy of an empty db
		if (config.cache == null || config.testWords != null) {
			Files.copy(Main.class.getResourceAsStream(EMPTY_DATABASE), Paths.get(dbFile), StandardCopyOption.REPLACE_EXISTING);
		} else {
			Path dbPath = Paths.get(config.cache);
			Files.copy(dbPath, new FileOutputStream(dbFile));
		}
		
		// socket sut, the interface with the mapper
		SocketSut sut = new SocketSut(sock);

		// Set up database
		Logger sqllog = new Logger(dbFile, config.sutName);

		// And test oracle
		Counter testQueryCounter = new Counter("Test Query Counter");
		MealyMembershipOracle<String, String> testOracle = new WordProcessor(sut, sqllog, true, testQueryCounter);
		if (config.testWords != null) {
			runTests(testOracle, config.testWords);
			System.exit(0);
		}
		
		testOracle = new NonDeterminismRetryingSutOracle<>(testOracle, config.maxNonDeterminismRetries, System.out);
		if (config.timeTimit != null) {
			testOracle = new TimeoutOracleWrapper<>(testOracle, config.timeTimit);
		}
		Integer roundLimit = config.roundLimit;

		// Create learnlib objects: membershipOracle, EquivalenceOracles and
		Counter membershipQueryCounter = new Counter("Membership Query Counter");
		MealyMembershipOracle<String, String> membershipOracle = new WordProcessor(sut, sqllog, true, membershipQueryCounter);
		membershipOracle = new NonDeterminismRetryingSutOracle<>(membershipOracle, config.maxNonDeterminismRetries, System.out);
		if (config.timeTimit != null) {
			membershipOracle = new TimeoutOracleWrapper<>(membershipOracle, config.timeTimit);
		}

		// build equivalence oracle
		MealyEquivalenceOracle<String, String> eqOracle = buildEqOracle(testOracle, config);

		
		MealyLearner<String, String> learner = null;
		// Set up the learner
		learner = new TTTLearnerMealy<String, String>(AlphabetFactory.generateInputAlphabet(config.alphabet), membershipOracle, AcexAnalyzers.BINARY_SEARCH_FWD);

		long start = System.currentTimeMillis();
		learner.startLearning();
		System.out.println("starting learning");
		MutableMealyMachine<?, String, ?, String> hyp = null;
		boolean done = false;
		int hypCounter = 0;
		int memQueries = 0;

		// Repeat until a hypothesis has been formed for which no counter
		// example can be found
		try {
			for (hypCounter = 0; !done; hypCounter++) {
				// the is a copy of the hypothesis model
				hyp = new FastMealy<>(new ListAlphabet<>(config.alphabet));
				AutomatonLowLevelCopy.copy(AutomatonCopyMethod.STATE_BY_STATE, learner.getHypothesisModel(), config.alphabet, hyp);
				// Print some stats
				statisticsFileStream
						.println("Hypothesis " + hypCounter + " after: " + (System.currentTimeMillis() - start) + "ms");
				printStatAndOut("Membership: " + (membershipQueryCounter.getValue() - memQueries));
				memQueries = membershipQueryCounter.getValue();
				
				GraphDOT.write(hyp, config.alphabet,  new FileWriter(expFolder + "hypothesis-" + hypCounter + ".dot"));
				if (roundLimit != null && hypCounter+1 >= roundLimit) {
					printStatAndOut("Learning stopped due to reaching the round limit");
					break;
				}
				
				@Nullable
				DefaultQuery<String, Word<String>> ce = eqOracle.findCounterExample(hyp, config.alphabet);
	
	
				// Check for a counterexample
				if (ce == null) {
					// No counterexample: close socket and done.
					System.out.println("No counterexample found; done!");
					sock.close();
					done = true;
				} else {
					// There is a counter example, send it to learnlib.
					printStatAndOut("Counter Example: " + ce);
					System.out.println("Sending Counter Example to LearnLib.");
					learner.refineHypothesis(ce);
				}
			}
		} catch(Exception e) {
			printStatAndOut("Learning stopped due to exception: " + e.getClass().getName());
			printStatAndOut("Exception message: " + e.getMessage());
		}

		// End of learning, update some stats
		long end = System.currentTimeMillis();
		printStatAndOut("Learning completed: " + done);
		printStatAndOut("Total mem Queries: " + membershipQueryCounter.getValue());
		printStatAndOut("Total test Queries: " + testQueryCounter.getValue());
		printStatAndOut("Timestamp: " + config.timestamp + ".");
		printStatAndOut("Running time: " + (end - start) + "ms.");
		statisticsFileStream.close();

		// Get result
		MealyMachine<?, String, ?, String>  learnedModel = hyp;

		return learnedModel;
	}
	
	private void runTests(MealyMembershipOracle<String, String> testOracle, List<Word<String>> testWords) {
		System.out.println("testWords parameter given, proceeding to run tests, then exit");
		for (Word<String> test : testWords) {
			Word<String> response = testOracle.answerQuery(test);
			System.out.println("Test Query: " + test);
			System.out.println("Output: " + response);
		}
	}

	public void printStatAndOut(String msg) {
		statisticsFileStream.println(msg);
		System.out.println(msg);
	}
	
	public MealyEquivalenceOracle<String, String> buildEqOracle(MealyMembershipOracle<String, String> oracle, Config config) {
		List<MealyEquivalenceOracle<String, String>> eqOracles = new ArrayList<>(config.equOracleTypes.size()); 
		for (EquType type : config.equOracleTypes) {
			switch(type) {
			case WORDS:
				eqOracles.add(new WordsEquivalenceOracle(config.eqTestWords, oracle));
				break;
			case RANDOM:
				eqOracles.add(new MealyRandomWpMethodEQOracle<>(oracle, 4, 5, config.maxNumTests));
				break;
			case EXHAUSTIVE:
				eqOracles.add(new MealyWpMethodEQOracle<>(oracle, 1));
				break;
			default:
				throw new RuntimeException("Equivalence oracle not supported");
			}
			
		}
		if (eqOracles.size() == 0) {
			throw new RuntimeException("Must supply at least one equivalence oracle type");
		} else if (eqOracles.size() == 1) {
			return eqOracles.get(0);
		} else {
			return new MealyEQOracleChain<String, String>(eqOracles);
		}
	}

	public static void copyToExpFolder(String expFolder, String... files) throws Exception {
		for (String file : files) {
			FileManager.copy(file, expFolder + new File(file).getName());
		}
	}
}