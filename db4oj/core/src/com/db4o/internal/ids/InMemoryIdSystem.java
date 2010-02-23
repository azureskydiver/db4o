/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class InMemoryIdSystem implements GlobalIdSystem {
	
	private Tree _ids;
	
	private int _idGenerator;
	

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public void commit(Visitable<SlotChange> slotChanges, Runnable commitBlock) {
		// slotChanges.accept(visitor)
		commitBlock.run();
		
	}

	public Slot committedSlot(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void completeInterruptedTransaction(int transactionId1,
			int transactionId2) {
		// TODO Auto-generated method stub
		
	}

	public int newId() {
		return ++ _idGenerator;
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		// TODO Auto-generated method stub
		
	}

}
