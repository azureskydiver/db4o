/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

import java.io.*;

import com.db4o.internal.*;



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
		if(jdkVersion() >= 4) {
			super.printStackTrace();
			return;
		}
		printStackTrace(System.err);
	}
	
	public void printStackTrace(PrintStream ps) {
		if(jdkVersion() >= 4) {
			super.printStackTrace(ps);
			return;
		}
		printStackTrace(new PrintWriter(ps, true));
	}

	public void printStackTrace(PrintWriter pw) {
		if(jdkVersion() >= 4) {
			super.printStackTrace(pw);
			return;
		}
		super.printStackTrace(pw);
		if (_cause != null) {
			pw.println("Nested cause:");
			_cause.printStackTrace(pw);
		}
	}

	private int jdkVersion() {
		return Platform4.jdk().ver();
	}

	public void superPrintStackTrace(PrintWriter s) {
		super.printStackTrace(s);
	}
}
