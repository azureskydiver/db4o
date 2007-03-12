/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.constraints;

import com.db4o.ext.*;


/**
 * base class for all constraint exceptions.
 */
public abstract class ConstraintViolationException extends Db4oException{

	public ConstraintViolationException(String msg) {
		super(msg);
	}

}
