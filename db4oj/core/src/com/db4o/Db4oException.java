package com.db4o;

public class Db4oException extends RuntimeException {
	private Exception _cause;
	
	public Db4oException(String msg) {
		super(msg);
	}

	public Db4oException(Exception cause) {
		this(cause.toString());
		_cause = cause;
	}
	
	public Db4oException(int messageConstant){
		this(Messages.get(messageConstant));
	}

	public Exception cause() {
		return _cause;
	}
}
