/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.ext;

/**
 * db4o-specific exception.<br><br>
 * This exception is thrown when the database file format
 * is not compatible with the applied configuration.
 */
public class IncompatibleFileFormatException extends Db4oException {

	public IncompatibleFileFormatException(){
		super();
	}
	
	public IncompatibleFileFormatException(String message) {
		super(message);
	}

}
