/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.polepos.idsys;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;


public class PlainLookupDriver extends IdSystemDriver {

	private int[] ids;

	public PlainLookupDriver(IdSystemEngine engine) {
		super(engine);
	}
		
	public void lapAllocate() {
		ids = new int[setup().getObjectCount()];
		for (int idIdx = 0; idIdx < ids.length; idIdx++) {
			ids[idIdx] = idSystem().newId();
		}
		idSystem().commit(new Visitable<SlotChange>() {
			public void accept(Visitor4<SlotChange> visitor) {
				for (int idIdx = 0; idIdx < ids.length; idIdx++) {
					SlotChange slotChange = new SlotChange(ids[idIdx]);
					slotChange.notifySlotCreated(new Slot(idIdx, 1));
					visitor.visit(slotChange);
				}
			}
		}, Runnable4.DO_NOTHING);
	}

	public void lapLookup() {
		for (int idIdx = 0; idIdx < ids.length; idIdx++) {
			idSystem().committedSlot(ids[idIdx]);
		}
	}

}
