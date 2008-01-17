/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.bench.delaying;

import com.db4o.ext.*;


public class InvalidDelayException extends Db4oException {

	public InvalidDelayException(String message) {
		super(message);
	}

}
