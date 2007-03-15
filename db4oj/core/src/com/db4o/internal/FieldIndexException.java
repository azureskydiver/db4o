/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.foundation.ChainedRuntimeException;

public class FieldIndexException extends ChainedRuntimeException {

	private FieldMetadata _field;
	
	public FieldIndexException(FieldMetadata field) {
		this(null,null,field);
	}

	public FieldIndexException(String msg,FieldMetadata field) {
		this(msg,null,field);
	}

	public FieldIndexException(Throwable cause,FieldMetadata field) {
		this(null,cause,field);
	}

	public FieldIndexException(String msg, Throwable cause,FieldMetadata field) {
		super(enhancedMessage(msg, field), cause);
		_field=field;
	}

	public FieldMetadata field() {
		return _field;
	}
	
	private static String enhancedMessage(String msg,FieldMetadata field) {
		String enhancedMessage="Field index for "+field.getParentYapClass().getName()+"#"+field.getName();
		if(msg!=null) {
			enhancedMessage+=": "+msg;
		}
		return enhancedMessage;
	}
}
