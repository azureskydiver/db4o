/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.slots.*;


/**
 * TODO: Check if all time-consuming stuff is overridden! 
 */
class TransactionObjectCarrier extends LocalTransaction{
	
	TransactionObjectCarrier(ObjectContainerBase container, Transaction parentTransaction, TransactionalReferenceSystem referenceSystem) {
		super(container, parentTransaction, referenceSystem);
	}
	
	public void commit() {
		// do nothing
	}
	
    public void slotFreeOnCommit(int id, Slot slot) {
//      do nothing
    }
    
    public void slotFreeOnRollback(int id, Slot slot) {
//      do nothing
    }
    
    void produceUpdateSlotChange(int id, Slot slot) {
        setPointer(id, slot);
    }
    
    void slotFreeOnRollbackCommitSetPointer(int id, Slot slot, boolean forFreespace) {
        setPointer(id, slot);
    }
    
    void slotFreePointerOnCommit(int a_id, Slot slot) {
//      do nothing
    }
    
    public void slotFreePointerOnCommit(int a_id) {
    	// do nothing
    }
	
	public void setPointer(int a_id, Slot slot) {
		writePointer(a_id, slot);
	}
    
    boolean supportsVirtualFields(){
        return false;
    }
    
    
    

}
