/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.ext;

import com.db4o.foundation.*;

public class CompositeDb4oException extends ChainedRuntimeException {

	public final Throwable[] _exceptions;
	
	public CompositeDb4oException(Throwable... exceptions) {
		_exceptions = exceptions;
	}
	
}
