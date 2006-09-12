package com.db4o.db4ounit.btree;

import com.db4o.inside.btree.*;

import db4ounit.*;



public class BTreeNullKeyTestCase extends BTreeTestCaseBase {	

	public void testSingleRemoveAddNull() {
		
		final Integer element = null;
		add(element);		
		assertSize(1);
		
		remove(element);		
		assertSize(0);
		
		add(element);
		
		assertSingleElement(element);
	}
	
	public void testMultipleNullKeys() {
		
		final Integer[] keys = new Integer[] { new Integer(1), null, new Integer(2), null, new Integer(3) };
		for (int idx = 0; idx < keys.length; idx++) {
			add(keys[idx]);
		}
		commit();
		BTreeRange range = _btree.search(trans(), null);
		Assert.areEqual(2,range.size());
	}

	public static void main(String[] args) {
		new BTreeNullKeyTestCase().runSolo();
	}
}
