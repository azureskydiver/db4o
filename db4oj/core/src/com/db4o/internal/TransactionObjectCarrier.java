/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;


/**
 * TODO: Check if all time-consuming stuff is overridden! 
 */
class TransactionObjectCarrier extends LocalTransaction{
	
	TransactionObjectCarrier(ObjectContainerBase a_stream, Transaction a_parent) {
		super(a_stream, a_parent);
	}
	
	public void commit() {
		// do nothing
	}
	
    public void slotFreeOnCommit(int a_id, int a_address, int a_length) {
//      do nothing
    }
    
    public void slotFreeOnRollback(int a_id, int a_address, int a_length) {
//      do nothing
    }
    
    void producteUpdateSlotChange(int a_id, int a_address, int a_length) {
        setPointer(a_id, a_address, a_length);
    }
    
    void slotFreeOnRollbackCommitSetPointer(int a_id, int newAddress, int newLength) {
        setPointer(a_id, newAddress, newLength);
    }
    
    void slotFreePointerOnCommit(int a_id, int a_address, int a_length) {
//      do nothing
    }
    
    public void slotFreePointerOnCommit(int a_id) {
    	// do nothing
    }
	
	public void setPointer(int a_id, int a_address, int a_length) {
		writePointer(a_id, a_address, a_length);
	}
    
    boolean supportsVirtualFields(){
        return false;
    }
    
    
    

}
