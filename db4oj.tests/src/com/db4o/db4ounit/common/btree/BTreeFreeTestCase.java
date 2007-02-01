/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.slots.*;

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
            Integer slotID = (Integer)allSlotIDs.current();
            Slot slot = fileTransaction().getCurrentSlotOfID(slotID.intValue());
            allSlots.add(slot);
        }
        
        YapFile yapFile = (YapFile)stream();
        
        
        final Collection4 freedSlots = new Collection4();
        
        yapFile.installDebugFreespaceManager(
            new FreespaceManagerForDebug(yapFile, new SlotListener() {
                public void onFree(Slot slot) {
                    freedSlots.add(slot);
                }
        }));
        
        _btree.free(systemTrans());
        
        systemTrans().commit();
        
        Assert.isTrue(freedSlots.containsAll(allSlots.iterator()));
        
    }

	private YapFileTransaction fileTransaction() {
		return ((YapFileTransaction)trans());
	}

}
