/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;


public class FreespaceManagerForDebug extends AbstractFreespaceManager {

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

    public void free(Slot slot) {
        _listener.onFree(slot);
    }

    public void freeSelf() {

    }

    public int totalFreespace() {
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

    public int shutdown() {
        return 0;
    }

}
