/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;


public class FreespaceManagerForDebug extends FreespaceManager {

    private final SlotListener _listener;

    public FreespaceManagerForDebug(LocalObjectContainer file, SlotListener listener) {
        super(file);
        _listener = listener;
    }

    public void beginCommit() {

    }

    public void debug() {
        
    }

    public void endCommit() {

    }

    public int entryCount() {
        return 0;
    }

    public void free(int address, int length) {
        _listener.onFree(new Slot(address, length));
    }

    public void freeSelf() {

    }

    public int freeSize() {
        return 0;
    }

    public int getSlot(int length) {
        return 0;
    }

    public void migrate(FreespaceManager newFM) {

    }

	public void onNew(LocalObjectContainer file) {
		
	}
	
    public void read(int freeSlotsID) {

    }

    public void start(int slotAddress) {

    }

    public byte systemType() {
        return FM_DEBUG;
    }

    public int write(boolean shuttingDown) {
        return 0;
    }

}
