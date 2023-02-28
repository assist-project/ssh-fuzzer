package learner;

public class NonDeterminismException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String oldOutput, newOutput, query;


	public NonDeterminismException(String query,
			String oldOutput, String newOutput) {
		this.query = query;
		this.oldOutput = oldOutput;
		this.newOutput = newOutput;
	}
	
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Non-determinism detected\n");
		sb.append("full input:\n");
		sb.append(query);
		sb.append("\nfull new output:\n");
		sb.append(newOutput);
		sb.append("\nold output:\n");
		sb.append(oldOutput);
		return sb.toString();
	}

	@Override
	public String toString() {
		return getMessage();
	}
}
