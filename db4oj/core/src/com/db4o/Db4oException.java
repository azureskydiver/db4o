package com.db4o;

public class Db4oException extends RuntimeException {
	private Exception _cause;
	
	public Db4oException(String msg) {
		super(msg);
	}

	public Db4oException(Exception cause) {
		this(cause.getMessage());
		_cause = cause;
	}

	public Exception cause() {
		return _cause;
	}
}
