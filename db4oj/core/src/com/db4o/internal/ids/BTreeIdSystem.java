/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.transactionlog.*;

/**
 * @exclude
 */
public class BTreeIdSystem implements GlobalIdSystem {

	public void close() {
		// TODO Auto-generated method stub
		
	}

	public Slot committedSlot(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	public InterruptedTransactionHandler interruptedTransactionHandler(
			int transactionId1, int transactionId2) {
		// TODO Auto-generated method stub
		return null;
	}

	public int newId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public IdSystemCommitContext prepareCommit(int slotChangeCount) {
		// TODO Auto-generated method stub
		return null;
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		// TODO Auto-generated method stub
		
	}

}
