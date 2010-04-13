/* Copyright (C) 2006  Versant Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;


public class FreespaceManagerForDebug implements FreespaceManager {

    private final SlotListener _listener;

    public FreespaceManagerForDebug(SlotListener listener) {
        _listener = listener;
    }
    
	public Slot allocateTransactionLogSlot(int length) {
		return null;
	}

	public void freeTransactionLogSlot(Slot slot) {
		
	}
	
    public void beginCommit() {

    }

	public void commit() {
		
	}
	
    public void endCommit() {

    }

    public int slotCount() {
        return 0;
    }

    public void free(Slot slot) {
        _listener.onFree(slot);
    }

    public void freeSelf() {

    }

	public Slot allocateSlot(int length) {
		return null;
	}

    public void read(LocalObjectContainer container, int freeSlotsID) {

    }

    public void start(int slotAddress) {

    }

    public byte systemType() {
        return AbstractFreespaceManager.FM_DEBUG;
    }

	public void traverse(Visitor4 visitor) {
		
	}
	
    public int write(LocalObjectContainer container) {
        return 0;
    }

	public void listener(FreespaceListener listener) {
		
	}

	public void migrateTo(FreespaceManager fm) {
		// TODO Auto-generated method stub
		
	}

	public int totalFreespace() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void slotFreed(Slot slot) {
		// TODO Auto-generated method stub
		
	}


}
