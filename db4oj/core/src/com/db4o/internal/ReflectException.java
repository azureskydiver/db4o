/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.ext.*;

/**
 * db4o-specific exception.<br><br>
 * This exception is thrown when one of the db4o 
 * reflection methods fails.
 *@see com.db4o.reflect
 */
public class ReflectException extends Db4oException {

	public ReflectException(Throwable cause) {
		super(cause);
	}
	
	public ReflectException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public Throwable getTarget() {
		return getCause();
	}
}
