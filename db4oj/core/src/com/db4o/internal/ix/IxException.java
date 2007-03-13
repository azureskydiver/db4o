/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal.ix;

import com.db4o.foundation.ChainedRuntimeException;

public class IxException extends ChainedRuntimeException {

	private int _address;
	private int _addressOffset;

	
	public IxException(int address, int addressOffset) {
		addressInfo(address, addressOffset);
	}

	private void addressInfo(int address, int addressOffset) {
		_address = address;
		_addressOffset = addressOffset;
	}

	public IxException(String msg, int address, int addressOffset) {
		super(msg);
		addressInfo(address, addressOffset);
	}

	public IxException(Throwable cause, int address, int addressOffset) {
		super(cause);
		addressInfo(address, addressOffset);
	}

	public IxException(String msg, Throwable cause, int address, int addressOffset) {
		super(msg, cause);
		addressInfo(address, addressOffset);
	}

	public int address() {
		return _address;
	}
	
	public int addressOffset() {
		return _addressOffset;
	}
}
