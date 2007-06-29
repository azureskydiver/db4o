package db4ounit;

public class AssertionException extends TestException {
	
	private static final long serialVersionUID = 900088031151055525L;

	public AssertionException(String message) {
		super(message, null);
	}
	
	public AssertionException(String message, Throwable cause) {
		super(message, cause);
	}
}
