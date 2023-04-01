package learner;

import java.util.Collection;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import de.learnlib.api.oracle.EquivalenceOracle.MealyEquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
import de.learnlib.api.query.DefaultQuery;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.words.Word;

public class WordsEquivalenceOracle implements MealyEquivalenceOracle<String, String> {
	private List<Word<String>> words;
	private MealyMembershipOracle<String, String> oracle;

	public WordsEquivalenceOracle( List<Word<String>> words, MealyMembershipOracle<String, String> membershipOracle) {
		this.words = words;
		this.oracle = membershipOracle;
	}

	@Override
	public @Nullable DefaultQuery<String, Word<String>> findCounterExample(
			MealyMachine<?, String, ?, String> hyp, Collection<? extends String> inputs) {
		DefaultQuery<String, Word<String>> ce = null;
		for (Word<String> testWord : words) {
			Word<String> outHyp = hyp.computeOutput(testWord);
			Word<String> outSut = oracle.answerQuery(testWord);
			if (!outHyp.equals(outSut)) {
				System.out.println("Words Oracle found counterexample: Sent: \"" + testWord + "\" \nExpected: \""
						+ outHyp + "\" \nGot: \"" + outSut + "\"");

				// Encapsulate the counterexample
				ce = new DefaultQuery<String, Word<String>>(testWord);
				ce.answer(outSut);
				break;
			}
		}
		return ce;
	}
}
