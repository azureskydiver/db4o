/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */
package com.db4o.internal;

import com.db4o.foundation.ChainedRuntimeException;

public class SlotRetrievalException extends ChainedRuntimeException {

	private int _id;
	
	public SlotRetrievalException(int id) {
		this(null,null,id);
	}

	public SlotRetrievalException(String msg,int id) {
		this(msg,null,id);
	}

	public SlotRetrievalException(Throwable cause,int id) {
		this(null,cause,id);
	}

	public SlotRetrievalException(String msg, Throwable cause,int id) {
		super(enhancedMessage(msg, id), cause);
		_id=id;
	}

	public int slotID() {
		return _id;
	}
	
	private static String enhancedMessage(String msg,int id) {
		String enhancedMessage="Slot ID "+id;
		if(msg!=null) {
			enhancedMessage+=": "+msg;
		}
		return enhancedMessage;
	}

}
