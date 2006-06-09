package db4ounit;

public class TestException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final Exception _cause;

	public TestException(Exception cause) {
		_cause = cause;
	}
	
	public Exception getCause() {
		return _cause;
	}

}
