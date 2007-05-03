/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o;

import com.db4o.foundation.*;

public class Db4oIOException extends ChainedRuntimeException {
	public Db4oIOException(Throwable e) {
		super(e.getMessage(), e);
	}
}
