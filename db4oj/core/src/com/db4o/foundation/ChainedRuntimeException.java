/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;


/**
 * @sharpen.ignore
 *
 * Adds supports for exception chaining for JDK 1.1
 */
public abstract class ChainedRuntimeException extends RuntimeException {
	
	private Throwable _cause;
	
	public ChainedRuntimeException() {
	}
	
	public ChainedRuntimeException(Throwable cause) {
		this(null, cause);
	}
	
	public ChainedRuntimeException(String msg) {
		this(msg, null);
	}
	
	public ChainedRuntimeException(String msg, Throwable cause) {
		super(msg);
		_cause = cause;
	}
	
	/**
	 * @return The originating exception, if any
	 */
	public final Throwable getCause() {
		return _cause;
	}
}
