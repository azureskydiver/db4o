/*
 * Copyright (C) 2005 db4objects Inc.  http://www.db4o.com
 */
package com.db4o.binding;

/**
 * Class CannotSaveException.  The exception that is thrown when a 
 * save/persist operation is requested but it cannot be performed for 
 * any reason.
 *
 * @author djo
 */
public class CannotSaveException extends Exception {
	
	/**
     * Constructor CannotSaveException.
     * 
	 * @param message The message.
	 */
	public CannotSaveException(String message) {
		super(message);
	}
	/**
     * Constructor CannotSaveException.
     * 
	 * @param message The message.
	 * @param cause The exception that was the real cause.
	 */
	public CannotSaveException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
     * Constructor CannotSaveException.
     * 
	 * @param cause The exception that was the real cause.
	 */
	public CannotSaveException(Throwable cause) {
		super(cause);
	}
}
