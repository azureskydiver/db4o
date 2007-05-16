/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

import db4ounit.*;


public class BTreeFreeTestCase extends BTreeTestCaseBase {

    private static final int[] VALUES = new int[] { 1, 2, 5, 7, 8, 9, 12 };

    public static void main(String[] args) {
        new BTreeFreeTestCase().runSolo();
    }
    
    public void test(){
        
        add(VALUES);
        
        Iterator4 allSlotIDs = _btree.allNodeIds(systemTrans());
        
        Collection4 allSlots = new Collection4();
        
        while(allSlotIDs.moveNext()){
            int slotID = ((Integer)allSlotIDs.current()).intValue();
            Slot slot = getSlotForID(slotID);
            allSlots.add(slot);
        }
        
        Slot bTreeSlot = getSlotForID(_btree.getID());
        allSlots.add(bTreeSlot);
        
        final LocalObjectContainer container = (LocalObjectContainer)stream();
        
        
        final Collection4 freedSlots = new Collection4();
        
        container.installDebugFreespaceManager(
            new FreespaceManagerForDebug(container, new SlotListener() {
                public void onFree(Slot slot) {
                    freedSlots.add(container.toNonBlockedLength(slot));
                }
        }));
        
        _btree.free(systemTrans());
        
        systemTrans().commit();
        
        Assert.isTrue(freedSlots.containsAll(allSlots.iterator()));
        
    }

    private Slot getSlotForID(int slotID) {
        return fileTransaction().getCurrentSlotOfID(slotID);
    }

	private LocalTransaction fileTransaction() {
		return ((LocalTransaction)trans());
	}

}
