package learner;


import java.time.Duration;

import de.ls5.jlearn.abstractclasses.LearningException;
import de.ls5.jlearn.interfaces.Oracle;
import de.ls5.jlearn.interfaces.Word;

public class TimeoutOracleWrapper implements Oracle {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long startTime;
	private Duration duration;
	private Oracle oracle;

	public TimeoutOracleWrapper(Oracle oracle, Duration duration) {
		this.oracle = oracle;
		this.startTime = System.currentTimeMillis();
		this.duration = duration;
	}

	@Override
	public Word processQuery(Word arg0) throws LearningException {
		if (System.currentTimeMillis() > duration.toMillis() + startTime) {
			throw new ExperimentTimeoutException(duration);
		}
		Word output = oracle.processQuery(arg0);
		return output;
	}
	
}
