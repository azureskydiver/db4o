/* Copyright (C) 2009 Versant Corporation http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.transactionlog.*;

/**
 * @exclude
 */
public interface IdSystem {

	public void addTransaction(LocalTransaction transaction);

	public void removeTransaction(LocalTransaction trans);

	public void collectSlotChanges(Transaction transaction,
			SlotChangeCollector collector);

	public boolean isDirty(Transaction transaction);

	public void commit(LocalTransaction transaction);

	public InterruptedTransactionHandler interruptedTransactionHandler(
			ByteArrayBuffer reader);

	public Slot getCommittedSlotOfID(LocalTransaction transaction, int id);

	public Slot getCurrentSlotOfID(LocalTransaction transaction, int id);

	public void slotFreeOnRollbackCommitSetPointer(
			LocalTransaction transaction, int id, Slot newSlot,
			boolean forFreespace);

	public void setPointer(Transaction transaction, int id, Slot slot);

	public void slotFreePointerOnCommit(LocalTransaction transaction, int id);

	public void slotDelete(Transaction transaction, int id, Slot slot);

	public void slotFreeOnCommit(Transaction transaction, int id, Slot slot);

	public void rollback(Transaction transaction);

	public void clear(Transaction transaction);

	public boolean isDeleted(Transaction transaction, int id);

	public void notifySlotChanged(Transaction transaction, int id, Slot slot);
	
	public void notifyNewSlotCreated(Transaction transaction, int id, Slot slot);

	public void systemTransaction(LocalTransaction transaction);

	public void close();

	public int newId(Transaction trans);

	public int prefetchID(Transaction transaction);

	public void prefetchedIDConsumed(Transaction transaction, int id);

}