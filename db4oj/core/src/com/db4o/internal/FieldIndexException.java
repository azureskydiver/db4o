/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.foundation.ChainedRuntimeException;

public class FieldIndexException extends ChainedRuntimeException {

	FieldMetadata _field;
	
	public FieldIndexException(FieldMetadata field) {
		_field=field;
	}

	public FieldIndexException(String msg,FieldMetadata field) {
		super(msg);
		_field=field;
	}

	public FieldIndexException(Throwable cause,FieldMetadata field) {
		super(cause);
		_field=field;
	}

	public FieldIndexException(String msg, Throwable cause,FieldMetadata field) {
		super(msg, cause);
		_field=field;
	}

	public FieldMetadata field() {
		return _field;
	}
}
