/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ids;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @exclude
 */
public class GlobalIdSystemTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {
	
	public static void main(String[] args) {
		new GlobalIdSystemTestCase().runSolo();
	}
	
	public void testSimpleSingleNewSlot(){
		LocalObjectContainer localContainer = (LocalObjectContainer) container();
		PointerBasedIdSystem idSystem = new PointerBasedIdSystem(localContainer);
		
		int newId = idSystem.newId();
		
		Slot oldSlot = idSystem.committedSlot(newId);
		Assert.isFalse(isValid(oldSlot));
		
		final SlotChange slotChange = SlotChangeFactory.USER_OBJECTS.newInstance(newId);
		int slotLength = 10;
		Slot allocatedSlot = localContainer.allocateSlot(slotLength);
		slotChange.notifySlotUpdated(localContainer, allocatedSlot);
		IdSystemCommitContext commitContext = idSystem.prepareCommit(1);
		commitContext.commit(new Visitable<SlotChange>() {
			public void accept(Visitor4<SlotChange> visitor) {
				visitor.visit(slotChange);
			}
		}, 1);
		
		Slot newSlot = idSystem.committedSlot(newId);
		Assert.areEqual(allocatedSlot, newSlot);
		
		
	}

	private boolean isValid(Slot slot) {
		return slot != null && ! slot.isNull();
	}
	
	

}
