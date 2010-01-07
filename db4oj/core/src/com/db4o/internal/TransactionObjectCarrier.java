/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.references.*;


/**
 * TODO: Check if all time-consuming stuff is overridden! 
 */
class TransactionObjectCarrier extends LocalTransaction{
	
	TransactionObjectCarrier(ObjectContainerBase container, Transaction parentTransaction, ReferenceSystem referenceSystem) {
		super(container, parentTransaction, referenceSystem);
	}
	
	public void commit() {
		// do nothing
	}
	
    boolean supportsVirtualFields(){
        return false;
    }

}
