/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.slots;

/**
 * @exclude
 */
public class FreespaceSlotChange extends SystemSlotChange {

	public FreespaceSlotChange(int id) {
		super(id);
	}
	
	public FreespaceSlotChange(int id, boolean forNewLogic) {
		super(id, forNewLogic);
	}
	
	
	protected void createNewLogic(int id){
		_newLogic = new FreespaceSlotChange(id, true);
	}


}
