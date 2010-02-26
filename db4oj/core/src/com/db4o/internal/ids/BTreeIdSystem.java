/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class BTreeIdSystem implements IdSystem {
	
	

	public BTreeIdSystem(int idSystemId) {
		// TODO Auto-generated constructor stub
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public Slot committedSlot(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void completeInterruptedTransaction(
			int transactionId1, int transactionId2) {
		// TODO Auto-generated method stub
	}

	public int newId() {
		
		
		
		// TODO Auto-generated method stub
		return 0;
	}

	public void commit(Visitable<SlotChange> slotChanges, Runnable commitBlock) {
		// TODO implement
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		// TODO Auto-generated method stub
		
	}

}
