/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.transactionlog.*;

/**
 * @exclude
 */
public interface GlobalIdSystem {

	public int newId();

	public Slot committedSlot(int id);

	public void returnUnusedIds(Visitable<Integer> visitable);

	public void close();

	public InterruptedTransactionHandler interruptedTransactionHandler(
			ByteArrayBuffer buffer);

	public IdSystemCommitContext prepareCommit(int slotChangeCount);

}