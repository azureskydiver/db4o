/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.slots;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class SystemSlotChange extends SlotChange {

	public SystemSlotChange(int id) {
		super(id);
	}
	
	public SystemSlotChange(int id, boolean forNewLogic){
		super(id, forNewLogic);
	}
	
	@Override
	protected void createNewLogic(int id) {
		_newLogic = new SystemSlotChange(id, true);
	}
	
	@Override
	public void freeDuringCommit(LocalObjectContainer file, boolean forFreespace) {
		super.freeDuringCommit(file, forFreespace);
		
		// FIXME: If we are doing a delete, we should also free our pointer here.
		
	}

}
