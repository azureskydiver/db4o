/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public abstract class FreespaceManagerTestCaseBase extends AbstractDb4oTestCase implements OptOutCS{
	
	protected FreespaceManager[] fm;
	
	protected void db4oSetupAfterStore() throws Exception {
		LocalObjectContainer container = (LocalObjectContainer) db();
		
		FreespaceManagerIx fmIx = new FreespaceManagerIx(container);
		int address = fmIx.onNew(container);
		fmIx.start(address);
		
		BTreeFreespaceManager btreeFm = new BTreeFreespaceManager(container);
		btreeFm.start(0);
		
		fm = new FreespaceManager[]{
			new RamFreespaceManager(container),
			// fmIx,
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
}
