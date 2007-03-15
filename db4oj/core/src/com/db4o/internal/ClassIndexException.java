/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.foundation.ChainedRuntimeException;

public class ClassIndexException extends ChainedRuntimeException {

	private ClassMetadata _class;
	
	public ClassIndexException(ClassMetadata clazz) {
		this(null,null,clazz);
	}

	public ClassIndexException(String msg,ClassMetadata clazz) {
		this(msg,null,clazz);
	}

	public ClassIndexException(Throwable cause,ClassMetadata clazz) {
		this(null,cause,clazz);
	}

	public ClassIndexException(String msg, Throwable cause,ClassMetadata clazz) {
		super(enhancedMessage(msg,clazz), cause);
		_class=clazz;
	}

	public ClassMetadata clazz() {
		return _class;
	}
	
	private static String enhancedMessage(String msg,ClassMetadata clazz) {
		String enhancedMessage="Class index for "+clazz.getName();
		if(msg!=null) {
			enhancedMessage+=": "+msg;
		}
		return enhancedMessage;
	}
}
