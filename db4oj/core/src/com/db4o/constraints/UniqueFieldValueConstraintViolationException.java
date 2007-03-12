/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.constraints;


/**
 * can be thrown by a UniqueFieldValueConstraint on commit.
 */
public class UniqueFieldValueConstraintViolationException extends ConstraintViolationException {

	public UniqueFieldValueConstraintViolationException(String className, String fieldName) {
		super("class: " + className + " field: " + fieldName);
	}

}
