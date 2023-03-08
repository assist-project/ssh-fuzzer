package learner;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;

import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
import de.learnlib.api.query.Query;
import net.automatalib.words.Word;

public class NonDeterminismRetryingSutOracle<I,O> implements MealyMembershipOracle<I, O> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MealyMembershipOracle<I, O> oracle;
	private PrintWriter log;
	private int retries;

	public NonDeterminismRetryingSutOracle(
			MealyMembershipOracle<I, O> oracle, int retries, OutputStream log) {
		this.log = new PrintWriter(log);
		this.oracle = oracle;
		this.retries = retries;
	}

	public Word<O> processQuery(Word<I> word) {
		int retries = 0;
		while (true) {
			try {
				Word<O> output = oracle.answerQuery(word);
				return output;
			} catch(NonDeterminismException nonDet) {
				if (retries < this.retries) {
					retries ++;
					log.println(String.format("Retry number %d of query %s", retries, word.toString()));
				} else {
					throw nonDet;
				}
			}
		}
	}

    public void processQueries(Collection<? extends Query<I, Word<O>>> queries) {
        for (Query<I, Word<O>> q : queries) {
            processQuery(q);
        }
    }
}
