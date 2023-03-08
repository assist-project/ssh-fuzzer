package learner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class Config {
	private static final String DEFAULT_CONFIG = "/default_config.prop";
	private static final String OLD_STATS_FILE = "input/statistics.txt"; // used
																		// to
																		// extract
																		// test
																		// words
																		// so as
																		// to
																		// recreate
																		// previous
																		// learning
																		// runs
	/*
	 * Example: equOracle="RANDOM;EXHAUSTIVE" maxNumTests=2000
	 */
	public final List<EquType> equOracleTypes;
	// applies only to equ oracles which otherwise would be unbounded.
	public final Integer maxNumTests;
	public final List<String> alphabet;
	public final List<String> testWords;
	public String specFile;
	public final String sutName;
	public final Duration timeTimit;
	public final Integer maxNonDeterminismRetries;
	public final String mapperHost;
	public final Integer mapperPort;
	public final String cache;
	public final Properties properties; 
	public final String outputFolder;
	public final long timestamp;

	public Config(String file) throws IOException {
		Properties defaults = new Properties();
		defaults.load(Main.class.getResourceAsStream(DEFAULT_CONFIG));
		properties = new Properties(defaults);
		if (!new File(file).exists()) {
			System.out.println("The property file " + properties + " doesn't exist. Taking all values from the default configuration default configuration.");
		} else {
			properties.load(new FileInputStream(file));
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
		testWords = new ArrayList<>();
		if (equOracleTypes.contains(EquType.WORDS)) {
			// getting test words from old stats file
			if (new File(OLD_STATS_FILE).exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(OLD_STATS_FILE));
				String str;
				while ((str = reader.readLine()) != null)
					if (str.startsWith("Counter Example:"))
						testWords.add(str.replace("Counter Example:", "").trim());
				reader.close();
			}

		}
		String timeLimit = properties.getProperty("timeLimit");
		if (timeLimit != null) {
			this.timeTimit = Duration.parse(timeLimit);
		} else {
			this.timeTimit = null;
		}
		maxNonDeterminismRetries = Integer.valueOf(properties.getProperty("maxNonDeterminismRetries"));
		cache = properties.getProperty("cache");
		timestamp = System.currentTimeMillis();
		outputFolder = "output/" + sutName + timestamp + "/";
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