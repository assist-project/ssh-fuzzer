package learner;

import java.util.List;

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

public abstract class AbstractEquivalenceOracle implements EquivalenceOracle {
	private Oracle oracle;
	private Automaton hyp;
    
	/**
	 * Initializes a test suite for the hypothesis to be tested.
	 */
    protected abstract void initialize(Automaton hyp); 
    
    
    @Override
	public final EquivalenceOracleOutput  findCounterExample(Automaton inputEnabledHypothesis) {
		this.hyp = inputEnabledHypothesis;
		EquivalenceOracleOutput counterExample = null;
		initialize(this.hyp);
		
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
						
						System.out.println(this.getClass().getSimpleName() + " found counterexample: Sent: \"" + testWord + "\" \nExpected: \"" + hyp.getTraceOutput(testWord) + "\" \nGot: \"" + testResult + "\"");
						
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
			
		} catch (LearningException e) {
			e.printStackTrace();
		}
		
		close();
		return null;
	}
    
    /**
     * Generates the next test or null (if the test suite has been exhausted)
     */
    protected abstract List<String> nextTest();
    
    public void setOracle(Oracle arg0) {
		this.oracle = arg0;
	}
    
    /**
     * Cleaning up once the test suite has been run
     */
    protected abstract void close(); 
}
