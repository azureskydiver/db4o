package db4ounit;

import java.io.PrintWriter;

/**
 * @sharpen.ignore
 */
public class TestException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final Exception _reason;

	public TestException(Exception reason) {
		_reason = reason;
	}
	
	public final Exception getReason() {
		return _reason;
	}
	
	public void printStackTrace(PrintWriter s) {
		if (null != _reason) {
			_reason.printStackTrace(s);
		} else {
			super.printStackTrace();
		}
		
	}
	
	public String toString() {
		return null != _reason
			? _reason.toString()
			: super.toString();
	}
}
