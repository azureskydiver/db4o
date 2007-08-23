/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.constraints;

import com.db4o.ext.*;

/**
 * Base class for all constraint exceptions.
 */
public class ConstraintViolationException extends Db4oException{

	/**
	 * ConstraintViolationException constructor with a specific 
	 * message.
	 * @param msg exception message
	 */
	public ConstraintViolationException(String msg) {
		super(msg);
	}

}
