/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import com.db4o.config.*;
import com.db4o.internal.slots.*;

import db4ounit.*;


public class FreespaceManagerDiscardLimitTestCase extends FreespaceManagerTestCaseBase{
	
	public static void main(String[] args) {
		new FreespaceManagerDiscardLimitTestCase().runSolo();
	}
	
	protected void configure(Configuration config) {
		config.freespace().discardSmallerThan(10);
	}
	
	public void testGetSlot(){
		for (int i = 0; i < fm.length; i++) {
			fm[i].free(new Slot(20,15));
			
			Slot slot = fm[i].getSlot(5);
			assertSlot(new Slot(20,5), slot);
			Assert.areEqual(1, fm[i].slotCount());
			fm[i].free(slot);
			Assert.areEqual(1, fm[i].slotCount());
			
			slot = fm[i].getSlot(6);
			assertSlot(new Slot(20,15), slot);
			Assert.areEqual(0, fm[i].slotCount());
			fm[i].free(slot);
			Assert.areEqual(0, fm[i].slotCount());
			slot = fm[i].getSlot(10);
			assertSlot(new Slot(20,15), slot);
			Assert.areEqual(0, fm[i].slotCount());
		}
	}
	
	

}
