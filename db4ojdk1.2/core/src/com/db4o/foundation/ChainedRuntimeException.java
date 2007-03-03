/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;


/**
 * Just delegates to the platform chaining mechanism.
 */
public abstract class ChainedRuntimeException extends RuntimeException {
	
	private Throwable _cause;
	
	public ChainedRuntimeException() {
	}
	
	public ChainedRuntimeException(String msg) {
		this(msg, null);
	}
	
	public ChainedRuntimeException(String msg, Throwable cause) {
		super(msg);
		_cause=cause;
	}
	
	public Throwable getCause() {
		return _cause;
	}
}
