/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.ix;

import com.db4o.foundation.*;

public class ComparableConversionException extends ChainedRuntimeException {

	private Object _unconverted;

	public ComparableConversionException(Object unconverted) {
		this(unconverted,null,null);
	}

	public ComparableConversionException(Object unconverted,Throwable cause) {
		this(unconverted,null,cause);
	}

	public ComparableConversionException(Object unconverted,String msg, Throwable cause) {
		super(msg,cause);
		_unconverted=unconverted;
	}

	public ComparableConversionException(Object unconverted,String msg) {
		this(unconverted,msg,null);
	}
	
	public Object unconverted() {
		return _unconverted;
	}
}
