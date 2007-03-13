/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.foundation.ChainedRuntimeException;

public class ClassIndexException extends ChainedRuntimeException {

	ClassMetadata _class;
	
	public ClassIndexException(ClassMetadata clazz) {
		_class=clazz;
	}

	public ClassIndexException(String msg,ClassMetadata clazz) {
		super(msg);
		_class=clazz;
	}

	public ClassIndexException(Throwable cause,ClassMetadata clazz) {
		super(cause);
		_class=clazz;
	}

	public ClassIndexException(String msg, Throwable cause,ClassMetadata clazz) {
		super(msg, cause);
		_class=clazz;
	}

	public ClassMetadata clazz() {
		return _class;
	}
}
