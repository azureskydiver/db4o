/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.foundation.*;

public class ClassIndexException extends ChainedRuntimeException {

	private String _className;
	
	public ClassIndexException(String className) {
		this(null,null,className);
	}

	public ClassIndexException(String msg,String className) {
		this(msg,null,className);
	}

	public ClassIndexException(Throwable cause,String className) {
		this(null,cause,className);
	}

	public ClassIndexException(String msg, Throwable cause,String className) {
		super(enhancedMessage(msg,className), cause);
		_className=className;
	}

	public String className() {
		return _className;
	}
	
	private static String enhancedMessage(String msg,String className) {
		String enhancedMessage="Class index for "+className;
		if(msg!=null) {
			enhancedMessage+=": "+msg;
		}
		return enhancedMessage;
	}
}
