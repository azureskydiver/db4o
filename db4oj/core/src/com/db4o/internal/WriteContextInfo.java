/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class WriteContextInfo {
	
	final boolean _isNew;
	
	private Slot _slot;

	public WriteContextInfo(boolean isNew, Slot slot) {
		_isNew = isNew;
		_slot = slot;
	}
	
	public Slot slot(){
		return _slot;
	}
	
	public void slot(Slot slot){
		_slot = slot;
	}
	
}
