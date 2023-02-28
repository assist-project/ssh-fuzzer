package learner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;

import de.ls5.jlearn.algorithms.packs.ObservationPack;
import de.ls5.jlearn.interfaces.Automaton;
import de.ls5.jlearn.interfaces.EquivalenceOracle;
import de.ls5.jlearn.interfaces.EquivalenceOracleOutput;
import de.ls5.jlearn.interfaces.Learner;
import de.ls5.jlearn.interfaces.Oracle;
import de.ls5.jlearn.interfaces.Word;
import de.ls5.jlearn.logging.LearnLog;
import de.ls5.jlearn.logging.LogLevel;
import de.ls5.jlearn.logging.PrintStreamLoggingAppender;
import de.ls5.jlearn.shared.AlphabetImpl;
import de.ls5.jlearn.shared.AutomatonImpl;
import de.ls5.jlearn.util.DotUtil;
import learner.Config.EquType;
import util.FileManager;
import util.SoundUtils;

public class Main {

	// input file(name)s
	private static final String emptyDbFile = "input/querylog-empty.db";
	private static final String defaultConfigFile = "input/config.prop";
	private static final String yannakakisNewExhaustiveCmd = "binaries/hybrid-ads -m all -p buggy -s hads -r 4 -x 70";
	private static final String yannakakisNewRandomCmd = "binaries/hybrid-ads -m random -p buggy -s hads -r 4 -x 70";

	// global hyp counter
	private static int hypCounter = 0;

	private PrintStream statisticsFileStream;

	public static void main(String[] args) {
		Main main = new Main();
		try {
			String configFile = Main.defaultConfigFile;
			if (args.length > 0) {
				configFile = args[0];
			}
			final Config config = new Config(configFile);
			Automaton learnedModel = main.learn(config);
			SoundUtils.success();
			File modelFile = new File(config.outputFolder + "learnedModel.dot");
			// Write final dot
			DotUtil.writeDot(learnedModel, modelFile);
			FileManager.copy(configFile, config.outputFolder + "config.prop");

		} catch (Exception e) {
			e.printStackTrace();
			SoundUtils.failure();
		}
	}

	public Main() {

	}
	
	public Automaton learn(Config config) throws Exception {
		String expFolder = config.outputFolder;
		if (!new File(expFolder).exists()) {
			new File(expFolder).mkdirs();
		}
		
		
		String outputFile = expFolder + "out.txt";
		String statisticsFile = expFolder + "statistics.txt";
		String dbFile = expFolder + "cache.db";
		String learnLog = expFolder + "learnLog.txt";
		
		// All output goes to file
		statisticsFileStream = new PrintStream(new FileOutputStream(statisticsFile, false));
		PrintStream fileStream = new PrintStream(new FileOutputStream(outputFile, false));
		System.setOut(fileStream);
		statisticsFileStream.println("Effective Configuration: " + config.getEffectiveConfigString());
		
		// Set up a connection
		Socket sock = new Socket(config.mapperHost, config.mapperPort);
		System.out.println("Created a shared socket on local port " + sock.getLocalPort());

		// Disable Nagle's delay algorithm
		sock.setTcpNoDelay(true);

		// Logging info goes to System.out
		LearnLog.addAppender(
				new PrintStreamLoggingAppender(LogLevel.DEBUG, new PrintStream(new FileOutputStream(learnLog, false))));

		// Some debug info
		System.out.println("Started the main process");
		System.out.println("Maximum number of traces: " + config.maxNumTests);
		System.out.println("Effective Configuration: " + config.getEffectiveConfigString());

		// if cache not present, make a copy of an empty db
		if (config.cache == null) {
			Path emptyDbPath = Paths.get(emptyDbFile);
			Files.copy(emptyDbPath, new FileOutputStream(dbFile));
		} else {
			Path dbPath = Paths.get(config.cache);
			Files.copy(dbPath, new FileOutputStream(dbFile));
		}
		
		// socket sut, the interface with the mapper
		SocketSut sut = new SocketSut(sock);

		// Set up database
		Logger sqllog = new Logger(dbFile, config.sutName);

		// And test oracle
		Oracle testOracle = new TestOracle(sut, sqllog);
		Counter testQueryCounter = ((TestOracle) testOracle).getQueryCounter();
		testOracle = new NonDeterminismRetryingSutOracle(testOracle, config.maxNonDeterminismRetries, System.out);
		if (config.timeTimit != null) {
			testOracle = new TimeoutOracleWrapper(testOracle, config.timeTimit);
		}

		// Create learnlib objects: membershipOracle, EquivalenceOracles and
		// Learner
		Oracle membershipOracle = new MembershipOracle(sut, sqllog);
		Counter membershipQueryCounter = ((MembershipOracle) membershipOracle).getQueryCounter();
		membershipOracle = new NonDeterminismRetryingSutOracle(membershipOracle, config.maxNonDeterminismRetries, System.out);
		if (config.timeTimit != null) {
			membershipOracle = new TimeoutOracleWrapper(membershipOracle, config.timeTimit);
		}

		// Set up the eqOracleMap (equType -> equOracle)
		EnumMap<EquType, EquivalenceOracle> equOracleMap = new EnumMap<EquType, EquivalenceOracle>(EquType.class);
		for (EquType equType : config.equOracleTypes) {
			EquivalenceOracle eqOracle = null;
			switch (equType) {
			case RANDOM:
				eqOracle = new YannakakisEquivalenceOracle(yannakakisNewRandomCmd, config.maxNumTests);
				break;
			case EXHAUSTIVE:
				eqOracle = new YannakakisEquivalenceOracle(yannakakisNewExhaustiveCmd);
				break;
			case WORDS:
				eqOracle = new WordsEquivalenceOracle(config.testWords);
				break;
			case CONFORMANCE:
				throw new RuntimeException("Operation not supported");
			}
			eqOracle.setOracle(testOracle);
			equOracleMap.put(equType, eqOracle);
		}

		Learner learner = null;

		// Set up the learner
		learner = new ObservationPack();
		learner.setOracle(membershipOracle);
		learner.setAlphabet(AlphabetFactory.generateInputAlphabet(config.alphabet));

		long start = System.currentTimeMillis();
		boolean done = false;
		int memQueries = 0, testQueries = 0;

		// Repeat until a hypothesis has been formed for which no counter
		// example can be found
		try {
			for (hypCounter = 0; !done; hypCounter++) {
				Automaton hyp = null;
	
				// Learn
				System.out.println("starting learning");
				learner.learn();
				System.out.println("done learning");
	
				// Print some stats
				statisticsFileStream
						.println("Hypothesis " + hypCounter + " after: " + (System.currentTimeMillis() - start) + "ms");
				statisticsFileStream.println("Membership: " + (membershipQueryCounter.getValue() - memQueries));
				memQueries = membershipQueryCounter.getValue();
				// memberOracle.resetNumQueries();
	
				// Retrieve hypothesis and write to dot file
				hyp = learner.getResult();
				DotUtil.writeDot(hyp, new File(expFolder + "hypothesis-" + hypCounter + ".dot"));
	
				EquivalenceOracleOutput equivOutput = null;
	
				for (EquType equOracleType : config.equOracleTypes) {
					EquivalenceOracle eqOracle = equOracleMap.get(equOracleType);
					System.out.println("starting " + equOracleType.name() + " equivalence query");
					// mapper.setRetrieveFromCache(false);
					equivOutput = eqOracle.findCounterExample(hyp);
					if (equivOutput != null)
						break;
					// mapper.setRetrieveFromCache(true);
					System.out.println("done " + equOracleType.name() + " equivalence query");
	
					statisticsFileStream
							.println(equOracleType.name() + " Equivalence: " + (testQueryCounter.getValue() - testQueries));
					testQueries = testQueryCounter.getValue();
				}
	
				// Check for a counterexample
				if (equivOutput == null) {
					// No counterexample: close socket and done.
					System.out.println("No counterexample found; done!");
					sock.close();
					done = true;
				} else {
					// There is a counter example, send it to learnlib.
					Word counterExample = equivOutput.getCounterExample();
	
					statisticsFileStream.println("Counter Example: " + counterExample.toString());
					System.out.println("Sending Counter Example to LearnLib.");
					System.out.println("Counter Example: " + counterExample.toString());
					learner.addCounterExample(counterExample, equivOutput.getOracleOutput());
				}
			}
		} catch(Exception e) {
			statisticsFileStream.println("Learning stopped due to exception: " + e.getClass().getName());
			statisticsFileStream.println("Exception message: " + e.getMessage());
		}

		// End of learning, update some stats
		long end = System.currentTimeMillis();
		statisticsFileStream.println("Learning completed: " + done);
		statisticsFileStream.println("Total mem Queries: " + membershipQueryCounter.getValue());
		statisticsFileStream.println("Total test Queries: " + testQueryCounter.getValue());
		statisticsFileStream.println("Timestamp: " + config.timestamp + ".");
		statisticsFileStream.println("Running time: " + (end - start) + "ms.");
		statisticsFileStream.close();

		// Get result
		Automaton learnedModel = learner.getResult();

		return learnedModel;
	}

	public static void copyToExpFolder(String expFolder, String... files) throws Exception {
		for (String file : files) {
			FileManager.copy(file, expFolder + new File(file).getName());
		}

	}

	public Automaton readExistingHypothesis(String filename) {
		System.out.println("Old hypothesis found");

		return new AutomatonImpl(new AlphabetImpl());
	}
}
