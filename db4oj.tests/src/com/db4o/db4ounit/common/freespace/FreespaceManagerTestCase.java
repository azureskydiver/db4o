/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class FreespaceManagerTestCase extends AbstractDb4oTestCase implements OptOutCS{
	
	private FreespaceManager[] fm;
	
	public static void main(String[] args) {
		new FreespaceManagerTestCase().runSolo();
	}
	
	protected void db4oSetupAfterStore() throws Exception {
		LocalObjectContainer container = (LocalObjectContainer) db();
		
		FreespaceManagerIx fmIx = new FreespaceManagerIx(container);
		int address = fmIx.onNew(container);
		fmIx.start(address);
		
		fm = new FreespaceManager[]{
			new FreespaceManagerRam(container),
			// fmIx,
			new BTreeFreespaceManager(container)
		};
	}
	
	public void testConstructor() {
		for (int i = 0; i < fm.length; i++) {
			Assert.areEqual(0, fm[i].slotCount());
			Assert.areEqual(0, fm[i].totalFreespace());
		}
	}
	
	public void testFree() {
		for (int i = 0; i < fm.length; i++) {
			int count = fm[i].slotCount();
			fm[i].free(new Slot(1000, 1));
			Assert.areEqual(count + 1, fm[i].slotCount());
		}
	}
	
	public void testGetSlot(){
		for (int i = 0; i < fm.length; i++) {
			Slot slot = fm[i].getSlot(1);
			Assert.isNull(slot);
			Assert.areEqual(0, fm[i].slotCount());
			
			fm[i].free(new Slot(10, 1));
			slot = fm[i].getSlot(1);
			Assert.areEqual(slot._address, 10);
			Assert.areEqual(0, fm[i].slotCount());
			
			fm[i].free(new Slot(10, 1));
			fm[i].free(new Slot(20, 2));
			slot = fm[i].getSlot(1);
			Assert.areEqual(1, fm[i].slotCount());
			Assert.areEqual(slot._address, 10);
			
			slot = fm[i].getSlot(3);
			Assert.isNull(slot);
			
			slot = fm[i].getSlot(1);
			Assert.isNotNull(slot);
			Assert.areEqual(1, fm[i].slotCount());

		}
	}
	
	public void testMerging() {
		for (int i = 0; i < fm.length; i++) {
			fm[i].free(new Slot(5, 5));
			fm[i].free(new Slot(15, 5));
			fm[i].free(new Slot(10, 5));
			Assert.areEqual(1, fm[i].slotCount());
		}
	}
	
	public void testTotalFreeSpace(){
		for (int i = 0; i < fm.length; i++) {
			fm[i].free(new Slot(5, 10));
			fm[i].free(new Slot(100, 5));
			fm[i].free(new Slot(140, 27));
			Assert.areEqual(42, fm[i].totalFreespace());
			fm[i].getSlot(8);
			Assert.areEqual(34, fm[i].totalFreespace());
			fm[i].getSlot(6);
			Assert.areEqual(28, fm[i].totalFreespace());
			fm[i].free(new Slot(120, 14));
			Assert.areEqual(42, fm[i].totalFreespace());
		}
	}
	
	public void testMigrateTo(){
		for (int from = 0; from < fm.length; from++) {
			for (int to = 0; to < fm.length; to++) {
				if(to != from){
					
					clear(fm[from]);
					clear(fm[to]);
					
					fm[from].migrateTo(fm[to]);
					assertSame(fm[from], fm[to]);
					
					fm[from].free(new Slot(5, 10));
					fm[from].free(new Slot(100, 5));
					fm[from].free(new Slot(140, 27));
					fm[from].migrateTo(fm[to]);
					
					assertSame(fm[from], fm[to]);
				}
			}
		}
	}
	
	private void clear(FreespaceManager freespaceManager){
		Slot slot = null;
		do{
			slot = freespaceManager.getSlot(1);
		}while(slot != null);
		Assert.areEqual(0, freespaceManager.slotCount());
		Assert.areEqual(0, freespaceManager.totalFreespace());
	}
	
	private void assertSame(FreespaceManager fm1, FreespaceManager fm2 ){
		Assert.areEqual(fm1.slotCount(), fm2.slotCount());
		Assert.areEqual(fm1.totalFreespace(), fm2.totalFreespace());
	}

}
