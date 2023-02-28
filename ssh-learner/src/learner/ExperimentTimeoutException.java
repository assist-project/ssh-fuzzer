package learner;

import java.time.Duration;

public class ExperimentTimeoutException extends RuntimeException {

	private Duration duration;

	public ExperimentTimeoutException(Duration duration) {
		this.duration = duration;
	}
	
	public String getMessage() {
		return String.format("Experiment timed out after a duration of %s", duration.toString());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
}
