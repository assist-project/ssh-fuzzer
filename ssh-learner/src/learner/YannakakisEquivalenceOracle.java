package learner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import de.ls5.jlearn.abstractclasses.LearningException;
import de.ls5.jlearn.equivalenceoracles.EquivalenceOracleOutputImpl;
//import util.Log;
import de.ls5.jlearn.interfaces.Automaton;
import de.ls5.jlearn.interfaces.EquivalenceOracle;
import de.ls5.jlearn.interfaces.EquivalenceOracleOutput;
import de.ls5.jlearn.interfaces.Oracle;
import de.ls5.jlearn.interfaces.Word;
import de.ls5.jlearn.shared.SymbolImpl;
import de.ls5.jlearn.shared.WordImpl;
import de.ls5.jlearn.util.DotUtil;

public class YannakakisEquivalenceOracle implements EquivalenceOracle {
    private final ProcessBuilder pb;
    private Process process;
    private Writer processInput;
    private BufferedReader processOutput;
    private StreamGobbler errorGobbler;
    private Automaton hyp;
    private Oracle oracle;
	private final int maxNumTests;
	private int crtTestNum;

    public YannakakisEquivalenceOracle(String yannakakisCmd) {
        this(yannakakisCmd, Integer.MAX_VALUE);
    }
    
    public YannakakisEquivalenceOracle(
    		//Automaton inputEnabledHypothesis,
            String yannakakisCmd, int maxNumTests) {
        this.pb = new ProcessBuilder(yannakakisCmd.split("\\s"));
        this.maxNumTests = maxNumTests;
        this.crtTestNum = 0;
    }

    public BufferedReader out() {
        return processOutput;
    }

    public void terminate() {
        close();
    }
    
    public void close() {
        closeAll();
    }

    public void initialize() {
        try {
            setupProcess();
            sendHypToProcess();
            this.crtTestNum = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHypToProcess() throws IOException {
        // the hyp is transformed to a dot state machine string
        StringWriter sw = new StringWriter();
        DotUtil.writeDot(hyp, sw);
        String dotString = sw.toString();

        // the dot string is modified to correspond with the dot version used by
        // the Yannakakis tool
        dotString = dotString
                .replaceAll(
                        "<<table border=\"0\" cellpadding=\"1\" cellspacing=\"0\"><tr><td>",
                        "\"");
        dotString = dotString.replaceAll("</td><td>", " ");
        dotString = dotString.replaceAll("</td></tr></table>>", "\"");

        // we input the dot string to the Yannakakis tool and flush
        processInput.append(dotString);
        processInput.flush();
    }
    
    
    private List<String> nextTest() throws IOException {
        List<String> nextTest = null;
        if (crtTestNum < maxNumTests) {
	        String line = out().readLine();
	        if ( line != null) {
	            nextTest = getNextTestFromLine(line);
	            crtTestNum ++;
	        }
	        
        }
        return nextTest;
    }

    private List<String> getNextTestFromLine(String line) {
        ArrayList<String> testQuery = new ArrayList<String>();

        Scanner s = new Scanner(line);
        while (s.hasNext()) {
            testQuery.add(s.next());
        }
        s.close();

        return testQuery;
    }

    /**
     * Strips the tags produces by learnlib 2 so that Yannakakis can use it
     */
    public String stripTagsFromDot(String dotWithTags) {
        return dotWithTags
                .replaceAll(
                        "<<table border=\"0\" cellpadding=\"1\" cellspacing=\"0\"><tr><td>",
                        "\"").replaceAll("</td><td>", " ")
                .replaceAll("</td></tr></table>>", "\"");
    }

    /**
     * A small class to print all stuff to stderr. Useful as I do not want
     * stderr and stdout of the external program to be merged, but still want to
     * redirect stderr to java's stderr.
     */
    class StreamGobbler extends Thread {
        private final InputStream stream;
        private final String prefix;

        StreamGobbler(InputStream stream, String prefix) {
            this.stream = stream;
            this.prefix = prefix;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null)
                    System.err.println(prefix + "> " + line);
            } catch (IOException e) {
                // It's fine if this thread crashes, nothing depends on it
                e.printStackTrace();
            }
        }
    }

    private boolean isClosed() {
        return process == null;
    }

    /**
     * Starts the process and creates buffered/whatnot streams for stdin stderr
     * or the external program
     * 
     * @throws IOException
     *             if the process could not be started (see ProcessBuilder.start
     *             for details).
     */
    private void setupProcess() throws IOException {
        process = pb.start();
        processInput = new OutputStreamWriter(process.getOutputStream());
        processOutput = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        errorGobbler = new StreamGobbler(process.getErrorStream(),
                "ERROR> main");
        errorGobbler.start();
    }

    /**
     * I thought this might be a good idea, but I'm not a native Java speaker,
     * so maybe it's not needed.
     */
    private void closeAll() {
        // Since we're closing, I think it's ok to continue in case of an
        // exception
        try {
            processInput.close();
            processOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            errorGobbler.join(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        process.destroy();
        process = null;
        processInput = null;
        processOutput = null;
        errorGobbler = null;
    }

	@Override
	public EquivalenceOracleOutput findCounterExample(Automaton inputEnabledHypothesis) {
		this.hyp = inputEnabledHypothesis;
		EquivalenceOracleOutput counterExample = null;
		initialize();
		
		try {
			List<String> test = nextTest();
			
			while(test != null) {
				if (oracle != null) {
					Word testWord = new WordImpl();
					
					//Build the Word to be tested from the Yannakakis-output
					for (String testSymbolString : test) {
						testWord.addSymbol(new SymbolImpl(testSymbolString));
					}
					
					//Query the SUL for the testword (this uses caching by default, should it be turned off?)
					Word testResult = oracle.processQuery(testWord);
					
					//If the query gives an unexpected result -> counterexample (hopefully equals() is implemented for Word, toString() comparison just to be sure)
					if (!hyp.getTraceOutput(testWord).equals(testResult) || !hyp.getTraceOutput(testWord).toString().equals(testResult.toString())) {
						
						System.out.println("Yannakakis found counterexample: Sent: \"" + testWord + "\" \nExpected: \"" + hyp.getTraceOutput(testWord) + "\" \nGot: \"" + testResult + "\"");
						
						//Encapsulate the counterexample
						counterExample = new EquivalenceOracleOutputImpl();
						counterExample.setCounterExample(testWord);
						counterExample.setOracleOutput(testResult); //Not sure if this should be the result of the query or the automaton
						
						//stop querying
						close();
						return counterExample;
					}
				}
				test = nextTest();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LearningException e) {
			e.printStackTrace();
		}
		
		close();
		return null;
	}

	@Override
	public void setOracle(Oracle oracle) {
		// TODO Auto-generated method stub
		this.oracle = oracle;
	}
}
