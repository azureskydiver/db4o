package com.db4o.internal;

public class UncheckedException extends RuntimeException {

	private Throwable _cause;

	public UncheckedException() {
		super();
	}

	public UncheckedException(String msg) {
		super(msg);
	}
	
	public UncheckedException(Throwable cause) {
		_cause = cause;
	}

	public Throwable getCause() {
		return _cause;
	}

}
