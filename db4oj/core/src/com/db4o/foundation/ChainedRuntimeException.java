/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

import java.io.*;


/**
 * @sharpen.ignore
 *
 * Adds supports for exception chaining for JDK 1.1
 */
public abstract class ChainedRuntimeException extends RuntimeException {
	
	public Throwable _cause;
	
	public ChainedRuntimeException() {
	}
	
	public ChainedRuntimeException(String msg) {
		this(msg,null);
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
	
	public void printStackTrace() {
		printStackTrace(System.err);
	}
	
	public void printStackTrace(PrintStream s) {
		printStackTrace(new PrintWriter(s));
	}
	
	public void printStackTrace(PrintWriter s) {
		super.printStackTrace(s);
		if(_cause!=null) {
			s.println("Nested cause:");
			_cause.printStackTrace(s);
		}
	}
}
