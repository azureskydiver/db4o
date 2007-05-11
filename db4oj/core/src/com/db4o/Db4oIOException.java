/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o;

import com.db4o.ext.*;

/**
 * db4o-specific exception.<br><br>
 * This exception is thrown when a system IO exception
 * is encounted by db4o process.
  */
public class Db4oIOException extends Db4oException {

	/**
	 * Constructor.
	 */
	public Db4oIOException() {
		super();
	}

	/**
	 * Constructor allowing to specify the causing exception
	 * @param cause exception cause 
	 */
	public Db4oIOException(Throwable cause) {
		super(cause.getMessage(), cause);
	}
}
