package learner;


import java.time.Duration;
import java.util.Collection;

import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
import de.learnlib.api.query.Query;
import net.automatalib.words.Word;

public class TimeoutOracleWrapper<I,O> implements MealyMembershipOracle<I, O> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long startTime;
	private Duration duration;
	private MealyMembershipOracle<I, O> oracle;

	public TimeoutOracleWrapper(MealyMembershipOracle<I, O> oracle, Duration duration) {
		this.oracle = oracle;
		this.startTime = System.currentTimeMillis();
		this.duration = duration;
	}

	@Override
	public void processQueries(Collection<? extends Query<I, Word<O>>> queries) {
		if (System.currentTimeMillis() > duration.toMillis() + startTime) {
			throw new ExperimentTimeoutException(duration);
		}
		oracle.processQueries(queries);
	}
}
