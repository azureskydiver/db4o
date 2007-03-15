/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal.ix;

import com.db4o.foundation.ChainedRuntimeException;

public class IxException extends ChainedRuntimeException {

	private int _address;
	private int _addressOffset;

	
	public IxException(int address, int addressOffset) {
		this(null,null,address,addressOffset);
	}

	public IxException(String msg, int address, int addressOffset) {
		this(msg,null,address,addressOffset);
	}

	public IxException(Throwable cause, int address, int addressOffset) {
		this(null,cause,address,addressOffset);
	}

	public IxException(String msg, Throwable cause, int address, int addressOffset) {
		super(enhancedMessage(msg, address, addressOffset), cause);
		_address=address;
		_addressOffset=addressOffset;
	}

	public int address() {
		return _address;
	}
	
	public int addressOffset() {
		return _addressOffset;
	}

	private static String enhancedMessage(String msg,int address,int addressOffset) {
		String enhancedMessage="Ix "+address+","+addressOffset;
		if(msg!=null) {
			enhancedMessage+=": "+msg;
		}
		return enhancedMessage;
	}
}
