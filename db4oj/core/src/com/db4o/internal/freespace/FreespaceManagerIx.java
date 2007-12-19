/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.Slot;

/**
 * Old freespacemanager, before version 7.0.
 * If it is still in use freespace is dropped.
 * {@link BTreeFreespaceManager} should be used instead.
 */
public class FreespaceManagerIx extends AbstractFreespaceManager{
    
    
    public FreespaceManagerIx(LocalObjectContainer file){
        super(file);
    }
    
	public Slot allocateTransactionLogSlot(int length) {
		throw new IllegalStateException();
	}
	
	public void freeTransactionLogSlot(Slot slot) {
		throw new IllegalStateException();
	}
    
    public void beginCommit() {
    	
    }
    
    public void endCommit() {
    	
    }
    
    public int slotCount() {
    	throw new IllegalStateException();
    }
    
    public void free(Slot slot) {
    	// Should no longer be used: Should not happen.
    	throw new IllegalStateException();
    }
    
    public void freeSelf() {
    	// do nothing, freespace is dropped.
    }
    
    public Slot getSlot(int length) {
    	throw new IllegalStateException();
	}

    public void migrateTo(FreespaceManager fm) {
    	// do nothing, freespace is dropped.
    }
    
	public void traverse(final Visitor4 visitor) {
    	throw new IllegalStateException();
	}
    
	public int onNew(LocalObjectContainer file) {
		return file.ensureFreespaceSlot();
	}
    
    public void read(int freespaceID) {
    	
    }
    
    public void start(int slotAddress) {
    	
    }
    
    public byte systemType() {
        return FM_IX;
    }

    public int write() {
        return 0;  
    }

	public void commit() {
		
	}

}
