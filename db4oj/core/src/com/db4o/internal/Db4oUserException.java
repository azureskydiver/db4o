/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.ext.*;

public class Db4oUserException extends Db4oException {

	public Db4oUserException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public Throwable getTarget() {
		return getCause();
	}
}
