/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.slots;

import com.db4o.foundation.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;

/**
 * @exclude
 */
public class FreespaceSlotChange extends SystemSlotChange {
	
	private Collection4 _freed;

	public FreespaceSlotChange(int id) {
		super(id);
	}
	
	@Override
	protected void free(FreespaceManager freespaceManager, Slot slot) {
		if(slot.isNull()){
			return;
		}
		if(_freed == null){
			_freed = new Collection4();
		}
		_freed.add(slot);
	}
	
	@Override
	protected boolean isForFreespace() {
		return true;
	}
	
	@Override
	public void freeDuringCommit(TransactionalIdSystem idSystem,
			FreespaceManager freespaceManager, boolean forFreespace) {
		super.freeDuringCommit(idSystem, freespaceManager, forFreespace);
		if(_freed == null){
			return;
		}
        if( isForFreespace() != forFreespace){
        	return;
        }
		Iterator4 iterator = _freed.iterator();
		while(iterator.moveNext()){
			Slot slot = (Slot) iterator.current();
			freespaceManager.free(slot);
		}
	}
}
