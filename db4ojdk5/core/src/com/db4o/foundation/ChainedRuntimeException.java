/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 *
 * Just delegates to the platform chaining mechanism.
 */
public abstract class ChainedRuntimeException extends RuntimeException {

	public ChainedRuntimeException() {
	}
	
	public ChainedRuntimeException(String msg) {
		this(msg,null);
	}
	
	public ChainedRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
