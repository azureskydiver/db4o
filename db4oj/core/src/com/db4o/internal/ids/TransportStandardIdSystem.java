/* Copyright (C) 2009 Versant Corporation http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.internal.transactionlog.*;

/**
 * @exclude
 */
public final class TransportStandardIdSystem implements IdSystem {
	
	private final LocalObjectContainer _container;
	
	public TransportStandardIdSystem(LocalObjectContainer localObjectContainer) {
		_container = localObjectContainer;
	}
	
	public int newId(Transaction transaction,
			SlotChangeFactory slotChangeFactory) {
		return localContainer().allocatePointerSlot();
	}
	
	public void notifySlotCreated(Transaction transaction, int id, Slot slot,
			SlotChangeFactory slotChangeFactory) {
		writePointer(id, slot);
	}

	private void writePointer(int id, Slot slot) {
		localContainer().writePointer(id, slot);
	}
	
	public void notifySlotChanged(Transaction transaction, int id, Slot slot,
			SlotChangeFactory slotChangeFactory) {
		writePointer(id, slot);
	}
	
	public void notifySlotDeleted(Transaction transaction, int id,
			SlotChangeFactory slotChangeFactory) {
		writePointer(id, Slot.ZERO);
	}
	
	protected StandardIdSlotChanges slotChanges(Transaction transaction) {
		throw new IllegalStateException();
	}
	
	public void commit(LocalTransaction transaction) {
		// don't do anything
	}
	
	public Slot getCurrentSlotOfID(LocalTransaction transaction, int id) {
		return getCommittedSlotOfID(id); 
	}
	
	public void addTransaction(LocalTransaction transaction) {
		// do nothing
	}
	
	public void removeTransaction(LocalTransaction transaction) {
		// do nothing
	}
	
	public void collectCallBackInfo(Transaction transaction,
			CallbackInfoCollector collector) {
		// do nothing
	}
	
	public void systemTransaction(LocalTransaction transaction) {
		// do nothing
	}
	
	public void close() {
		// do nothing
	}
	
	public LocalObjectContainer localContainer() {
		return _container;
	}

	public void clear(Transaction transaction) {
		// TODO Auto-generated method stub
		
	}

	public Slot getCommittedSlotOfID(int id) {
		return localContainer().readPointer(id)._slot;
	}

	public InterruptedTransactionHandler interruptedTransactionHandler(
			ByteArrayBuffer reader) {
		return null;
	}

	public boolean isDeleted(Transaction transaction, int id) {
		return false;
	}

	public boolean isDirty(Transaction transaction) {
		return false;
	}

	public int prefetchID(Transaction transaction) {
		return 0;
	}

	public void prefetchedIDConsumed(Transaction transaction, int id) {
		
	}

	public void rollback(Transaction transaction) {
		
	}

}
