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

	public void collectCallBackInfo(Transaction transaction,
			CallbackInfoCollector collector);

	public boolean isDirty(Transaction transaction);

	public void commit(LocalTransaction transaction);

	public InterruptedTransactionHandler interruptedTransactionHandler(
			int transactionId1, int transactionId2);

	public Slot committedSlot(int id);

	public Slot currentSlot(LocalTransaction transaction, int id);

	public void rollback(Transaction transaction);

	public void clear(Transaction transaction);

	public boolean isDeleted(Transaction transaction, int id);

	public void notifySlotUpdated(Transaction transaction, int id, Slot slot, SlotChangeFactory slotChangeFactory);
	
	public void notifySlotCreated(Transaction transaction, int id, Slot slot, SlotChangeFactory slotChangeFactory);
	
	public void notifySlotDeleted(Transaction transaction, int id, SlotChangeFactory slotChangeFactory);

	public void systemTransaction(LocalTransaction transaction);

	public void close();

	public int newId(Transaction trans, SlotChangeFactory slotChangeFactory);

	public int prefetchID(Transaction transaction);

	public void prefetchedIDConsumed(Transaction transaction, int id);

}