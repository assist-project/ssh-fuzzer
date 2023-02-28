package learner;

import java.io.OutputStream;
import java.io.PrintWriter;

import de.ls5.jlearn.abstractclasses.LearningException;
import de.ls5.jlearn.interfaces.Oracle;
import de.ls5.jlearn.interfaces.Word;

public class NonDeterminismRetryingSutOracle implements Oracle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Oracle oracle;
	private PrintWriter log;
	private int retries;

	public NonDeterminismRetryingSutOracle(
			Oracle oracle, int retries, OutputStream log) {
		this.log = new PrintWriter(log);
		this.oracle = oracle;
		this.retries = retries;
	}

	@Override
	public Word processQuery(Word word) throws LearningException {
		int retries = 0;
		while (true) {
			try {
				Word output = oracle.processQuery(word);
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

}
