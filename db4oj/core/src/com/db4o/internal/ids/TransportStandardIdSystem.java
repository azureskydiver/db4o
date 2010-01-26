/* Copyright (C) 2009 Versant Corporation http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class TransportStandardIdSystem extends StandardIdSystem {
	
	public TransportStandardIdSystem(LocalObjectContainer localObjectContainer) {
		super(localObjectContainer);
	}

	@Override
	protected void slotFreePointerOnRollback(Transaction transaction, int id, SlotChangeFactory slotChangeFactory) {
		// do nothing
	}
	
	@Override
	public void slotFreeOnRollbackCommitSetPointer(LocalTransaction transaction, int id, Slot slot, boolean forFreespace, SlotChangeFactory slotChangeFactory) {
        setPointer(transaction, id, slot);
    }
    
	@Override
	public void setPointer(Transaction transaction, int id, Slot slot) {
		localContainer().writePointer(id, slot);
	}
	
	@Override
	public void slotFreePointerOnCommit(LocalTransaction transaction, int id, SlotChangeFactory slotChangeFactory, boolean isFreespace) {
		// do nothing
	}
	
	@Override
	public void slotFreeOnCommit(Transaction transaction, int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		// do nothing
	}
	
	@Override
	protected void slotFreeOnRollback(Transaction transaction, int id, Slot slot) {
		// do nothing
	}
	
	@Override
	public void oldNotifySlotChanged(Transaction transaction, int id, Slot slot, boolean forFreespace) {
		setPointer(transaction, id, slot);
	}

}
