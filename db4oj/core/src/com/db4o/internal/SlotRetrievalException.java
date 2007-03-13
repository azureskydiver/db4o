/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.foundation.ChainedRuntimeException;

public class SlotRetrievalException extends ChainedRuntimeException {

	private int _id;
	
	public SlotRetrievalException(int id) {
		_id=id;
	}

	public SlotRetrievalException(String msg,int id) {
		super(msg);
		_id=id;
	}

	public SlotRetrievalException(Throwable cause,int id) {
		super(cause);
		_id=id;
	}

	public SlotRetrievalException(String msg, Throwable cause,int id) {
		super(msg, cause);
		_id=id;
	}

	public int slotID() {
		return _id;
	}
}
