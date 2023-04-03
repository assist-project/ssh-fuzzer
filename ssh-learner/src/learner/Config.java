package learner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import net.automatalib.words.Word;

public class Config {
	private static final String DEFAULT_CONFIG = "/default_config.prop";
	
	private static String [] PROPERTY_NAMES = new String [] {"name",
			"mapperAddress",
			"eqOracle",
			"specification",
			"maxNumTests",
			"timeLimit",
			"roundLimit",
			"cache",
			"maxNonDeterminismRetries",
			"testWords",
			"eqTestWords",
			"mapperAddress",
			"alphabet"
	}
	;
	/*
	 * Example: equOracle="RANDOM;EXHAUSTIVE" maxNumTests=2000
	 */
	public final List<EquType> equOracleTypes;
	// applies only to equ oracles which otherwise would be unbounded.
	public final Integer maxNumTests;
	public final List<String> alphabet;
	public final List<Word<String>> testWords;
	public final List<Word<String>> eqTestWords;
	public String specFile;
	public final String sutName;
	public final Duration timeTimit;
	public final Integer roundLimit;
	public final Integer maxNonDeterminismRetries;
	public final String mapperHost;
	public final Integer mapperPort;
	public final String cache;
	public final Properties properties; 
	public final String outputFolder;
	public final long timestamp;

	public Config(String file, String ... args) throws IOException {
		Properties defaults = new Properties();
		defaults.load(Main.class.getResourceAsStream(DEFAULT_CONFIG));
		properties = new Properties(defaults);
		properties.load(new FileInputStream(file));
		for (String arg : args) {
			String[] prop = arg.split("=");
			if (prop.length != 2) {
				throw new RuntimeException("Arguments after config file are of for 'param=value'");
			}
			properties.put(prop[0], prop[1]);
		}
		
		Set<String> names = new LinkedHashSet<>(properties.stringPropertyNames());
		names.removeAll(Sets.newHashSet(PROPERTY_NAMES));
		if (!names.isEmpty()) {
			throw new RuntimeException("Unrecognized parameter(s): " + names + System.lineSeparator() + "Supported parameters are: " + Arrays.asList(PROPERTY_NAMES));
		}
		
		this.sutName = (String) properties.getProperty("name");
		String mapperAddress = properties.getProperty("mapperAddress");
		String [] hostPort = mapperAddress.split("\\:");
		mapperHost = hostPort[0];
		mapperPort = Integer.valueOf(hostPort[1]);
		
		String equOraclesString = properties.getProperty("eqOracle");
		equOracleTypes = new ArrayList<EquType>();
		for (String equOracleStr : equOraclesString.split(";")) {
			EquType equOracle = EquType.valueOf(equOracleStr.trim().toUpperCase());
			if (equOracle == null)
				throw new RuntimeException(
						"Invalid equ oracle" + equOracleStr + ". Select from " + Arrays.toString(EquType.values()));
			equOracleTypes.add(equOracle);
		}
		String[] inputs = properties.get("alphabet").toString().split(";");
		alphabet = Arrays.stream(inputs).map(str -> str.toUpperCase()).filter(str -> !str.startsWith("!")) // !
																											// is
																											// used
																											// for
																											// commented
																											// inputs
				.collect(Collectors.<String>toList());

		maxNumTests = Integer.valueOf(properties.getProperty("maxNumTests"));
		String testWordsConfig = properties.getProperty("testWords");
		if (testWordsConfig != null) {
			this.testWords = new ArrayList<>();
			readTestWords(this.testWords, testWordsConfig);
		} else {
			this.testWords = null;
		}
		
		String eqTestWordsConfig = properties.getProperty("eqTestWords");
		if (eqTestWordsConfig != null) {
			eqTestWords = new ArrayList<>();
			readTestWords(eqTestWords, eqTestWordsConfig);
		} else {
			eqTestWords = null;
		}
		
		if (equOracleTypes.contains(EquType.WORDS)) {
			if (eqTestWords == null) {
				throw new RuntimeException("For WORDS equivalence oracle, eqTestWords parameter should be set to a file containing test words");
			}
		}
		
		String timeLimit = properties.getProperty("timeLimit");
		if (timeLimit != null) {
			this.timeTimit = Duration.parse(timeLimit);
		} else {
			this.timeTimit = null;
		}
		String roundLimit = properties.getProperty("roundLimit");
		if (roundLimit != null) {
			this.roundLimit = Integer.valueOf(roundLimit);
		} else {
			this.roundLimit = null;
		}
		maxNonDeterminismRetries = Integer.valueOf(properties.getProperty("maxNonDeterminismRetries"));
		cache = properties.getProperty("cache");
		timestamp = System.currentTimeMillis();
		outputFolder = "output/" + sutName + timestamp + "/";
	}
	
	private void readTestWords(Collection<Word<String>> words, String fileOrTest) throws IOException {
		Reader reader;
		if (new File(fileOrTest).exists()) {
			reader = new FileReader(fileOrTest);
		} else {
			reader = new StringReader(fileOrTest);
		}
		TestParser parser = new TestParser();
		words.addAll(parser.readTests(alphabet, reader));
	}
	
	public void export(OutputStream outputStream) {
		PrintWriter pw = new PrintWriter(outputStream);
		pw.append(properties.toString());
		
	}

	public String getEffectiveConfigString() {
		return properties.toString();
	}
	
	public static enum EquType {
		RANDOM, EXHAUSTIVE, WORDS
	}
	
}