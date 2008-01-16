/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


public abstract class FreespaceManagerTestCaseBase extends FileSizeTestCaseBase implements OptOutCS{
	
	protected FreespaceManager[] fm;
	
	protected void db4oSetupAfterStore() throws Exception {
		LocalObjectContainer container = (LocalObjectContainer) db();
		
		BTreeFreespaceManager btreeFm = new BTreeFreespaceManager(container);
		btreeFm.start(0);
		
		fm = new FreespaceManager[]{
			new RamFreespaceManager(container),
			btreeFm,
		};
	}
	
	protected void clear(FreespaceManager freespaceManager){
		Slot slot = null;
		do{
			slot = freespaceManager.getSlot(1);
		}while(slot != null);
		Assert.areEqual(0, freespaceManager.slotCount());
		Assert.areEqual(0, freespaceManager.totalFreespace());
	}
	
	protected void assertSame(FreespaceManager fm1, FreespaceManager fm2 ){
		Assert.areEqual(fm1.slotCount(), fm2.slotCount());
		Assert.areEqual(fm1.totalFreespace(), fm2.totalFreespace());
	}

	protected void assertSlot(Slot expected, Slot actual){
		Assert.areEqual(expected, actual);
	}
    
    protected void produceSomeFreeSpace() {
        FreespaceManager fm = currentFreespaceManager();
        int length = 300;
        Slot slot = container().getSlot(length);
        BufferImpl buffer = new BufferImpl(length);
        container().writeBytes(buffer, slot.address(), 0);
        fm.free(slot);
    }

    protected FreespaceManager currentFreespaceManager() {
        return container().freespaceManager();
    }
    
    public static class Item{
        public int _int; 
    }

     protected void storeSomeItems() {
        for (int i = 0; i < 3; i++) {
            store(new Item());
        }
        db().commit();
    }
    
    protected LocalObjectContainer container() {
        return fixture().fileSession();
    }

}
