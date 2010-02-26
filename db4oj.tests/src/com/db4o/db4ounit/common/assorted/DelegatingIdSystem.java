/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

public class DelegatingIdSystem implements IdSystem {
	
	protected final IdSystem _delegate;
	
	public DelegatingIdSystem(LocalObjectContainer container, int idSystemId){
		_delegate = new InMemoryIdSystem(container);
	}

	public void close() {
		_delegate.close();
	}

	public void commit(Visitable<SlotChange> slotChanges,
			Runnable commitBlock) {
		_delegate.commit(slotChanges, commitBlock);
	}

	public Slot committedSlot(int id) {
		return _delegate.committedSlot(id);
	}

	public void completeInterruptedTransaction(int transactionId1,
			int transactionId2) {
		_delegate.completeInterruptedTransaction(transactionId1, transactionId2);
	}

	public int newId() {
		return _delegate.newId();
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		_delegate.returnUnusedIds(visitable);
	}
	

}
