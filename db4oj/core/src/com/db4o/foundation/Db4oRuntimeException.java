/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.foundation;

public class Db4oRuntimeException extends ChainedRuntimeException {

	public Db4oRuntimeException() {
		super();
	}

	public Db4oRuntimeException(Throwable cause) {
		super(cause);
	}

	public Db4oRuntimeException(String msg) {
		super(msg);
	}

	public Db4oRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
