package learner;

/**
 * Wraps around exceptions in the interaction with the external Mappper component.
 */
public class MapperException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MapperException(String message) {
		super(message);
	}
	
	public MapperException(Exception e) {
        super(e);
    }
	
	public MapperException(String message, Exception e) {
        super(message, e);
    }
}
