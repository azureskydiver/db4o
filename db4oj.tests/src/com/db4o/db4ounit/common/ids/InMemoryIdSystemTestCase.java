/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ids;

import com.db4o.foundation.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

/**
 * @exclude
 */
public class InMemoryIdSystemTestCase implements TestLifeCycle {
	
	private GlobalIdSystem _idSystem;
	
	public void testEmpty(){
		Assert.isNull(_idSystem.committedSlot(1));
		Assert.isGreater(0, _idSystem.newId());
		final BooleanByRef commitBlockWasRun = new BooleanByRef();
		_idSystem.commit(new Visitable<SlotChange>() {
			
			public void accept(Visitor4<SlotChange> visitor) {
				// TODO Auto-generated method stub
				
			}
		}, new Runnable() {
			
			public void run() {
				commitBlockWasRun.value = true;
			}
		});
		
		Assert.isTrue(commitBlockWasRun.value);
	}
	
	public void testSingleCommit(){
		int id = _idSystem.newId();
		final SlotChange slotChange = new SlotChange(id);
		Slot newSlot = new Slot(1, 2);
		slotChange.notifySlotCreated(newSlot);
		_idSystem.commit(new Visitable<SlotChange>() {
			public void accept(Visitor4<SlotChange> visitor) {
				visitor.visit(slotChange);
			}
		}, new Runnable() {
			public void run() {
			}
		});
		
		Slot committedSlot = _idSystem.committedSlot(id);
		Assert.areEqual(newSlot, committedSlot);
	}
	

	public void setUp() throws Exception {
		_idSystem = new InMemoryIdSystem();
	}

	public void tearDown() throws Exception {
		
	}
	
	

}
