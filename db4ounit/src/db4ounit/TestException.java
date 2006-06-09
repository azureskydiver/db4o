package db4ounit;

public class TestException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final Exception _reason;

	public TestException(Exception reason) {
		_reason = reason;
	}
	
	public final Exception getReason() {
		return _reason;
	}
}
