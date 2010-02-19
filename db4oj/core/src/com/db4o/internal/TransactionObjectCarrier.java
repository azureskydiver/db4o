/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.ids.*;
import com.db4o.internal.references.*;


/**
 * TODO: Check if all time-consuming stuff is overridden! 
 */
class TransactionObjectCarrier extends LocalTransaction{
	
	private final IdSystem _idSystem;
	
	TransactionObjectCarrier(ObjectContainerBase container, Transaction parentTransaction, IdSystem idSystem, ReferenceSystem referenceSystem) {
		super(container, parentTransaction, idSystem, referenceSystem);
		_idSystem = idSystem;
	}
	
	public void commit() {
		// do nothing
	}
	
    boolean supportsVirtualFields(){
        return false;
    }
    

}
