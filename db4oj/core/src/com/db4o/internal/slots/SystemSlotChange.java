/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.slots;

import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;

/**
 * @exclude
 */
public class SystemSlotChange extends SlotChange {

	public SystemSlotChange(int id) {
		super(id);
	}
	
	@Override
	public void freeDuringCommit(TransactionalIdSystem idSystem,
			FreespaceManager freespaceManager, boolean forFreespace) {
		super.freeDuringCommit(idSystem, freespaceManager, forFreespace);
		
		// FIXME: If we are doing a delete, we should also free our pointer here.
		
	}
	
	@Override
	protected Slot modifiedSlotInUnderlyingIdSystem(TransactionalIdSystem idSystem) {
		return null;
	}

}
