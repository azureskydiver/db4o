/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.extensions.*;


public class FreespaceManagerForDebug extends AbstractFreespaceManager {

    private final SlotListener _listener;

    public FreespaceManagerForDebug(LocalObjectContainer file, SlotListener listener) {
        super(file);
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

	public Slot getSlot(int length) {
		return null;
	}

    public void read(int freeSlotsID) {

    }

    public void start(int slotAddress) {

    }

    public byte systemType() {
        return FM_DEBUG;
    }

	public void traverse(Visitor4 visitor) {
		
	}
	
    public int write() {
        return 0;
    }


}
