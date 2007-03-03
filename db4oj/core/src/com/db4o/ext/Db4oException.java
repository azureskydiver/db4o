/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.ext;

import com.db4o.foundation.ChainedRuntimeException;
import com.db4o.internal.*;

/**
 * db4o exception wrapper: Exceptions occurring during internal processing
 * will be proliferated to the client calling code encapsulated in an exception
 * of rhis type. The original exception, if any, is available through
 * {@link Db4oException#cause()}.
 */
public class Db4oException extends ChainedRuntimeException {
	public Db4oException(String msg) {
		super(msg);
	}

	public Db4oException(Exception cause) {
		super(cause.getMessage(), cause);
	}
	
	public Db4oException(int messageConstant){
		this(Messages.get(messageConstant));
	}
}
