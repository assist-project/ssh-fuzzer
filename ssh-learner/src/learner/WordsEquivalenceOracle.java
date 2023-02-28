package learner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.ls5.jlearn.abstractclasses.LearningException;
import de.ls5.jlearn.equivalenceoracles.EquivalenceOracleOutputImpl;
import de.ls5.jlearn.interfaces.Automaton;
import de.ls5.jlearn.interfaces.EquivalenceOracle;
import de.ls5.jlearn.interfaces.EquivalenceOracleOutput;
import de.ls5.jlearn.interfaces.Oracle;
import de.ls5.jlearn.interfaces.Symbol;
import de.ls5.jlearn.interfaces.Word;
import de.ls5.jlearn.shared.SymbolImpl;
import de.ls5.jlearn.shared.WordImpl;

public class WordsEquivalenceOracle implements EquivalenceOracle {
	private List<Word> words;
	private Oracle oracle;

	public WordsEquivalenceOracle(List<String> tracesToTest) {
		List<Word> wordsToTest = tracesToTest.stream().map(traceString -> (Word) new WordImpl(
				Arrays.stream(traceString.split(" ")).map(s -> new SymbolImpl(s.trim())).toArray(Symbol[]::new)))
				.collect(Collectors.toList());
		this.words = wordsToTest;
	}

	public EquivalenceOracleOutput findCounterExample(Automaton hyp) {
		try {
			for (Word testWord : words) {
				Word outHyp = hyp.getTraceOutput(testWord);
				Word outSut = oracle.processQuery(testWord);
				if (!outHyp.equals(outSut)) {
					System.out.println("Words Oracle found counterexample: Sent: \"" + testWord + "\" \nExpected: \""
							+ outHyp + "\" \nGot: \"" + outSut + "\"");

					// Encapsulate the counterexample
					EquivalenceOracleOutputImpl counterExample = new EquivalenceOracleOutputImpl();
					counterExample.setCounterExample(testWord);
					counterExample.setOracleOutput(outSut); 
					return counterExample;
				}
			}
		} catch (LearningException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setOracle(Oracle arg0) {
		this.oracle = arg0;
	}
}
