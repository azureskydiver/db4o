/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.fieldindex;

import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;

/**
 * Jira ticket: COR-373
 * 
 * @exclude
 */
public class StringIndexCorruptionTestCase extends StringIndexTestCaseBase {
	
	protected void configure(Configuration config) {
		super.configure(config);
	    config.flushFileBuffers(false); // this just make the test faster
	}
	
	public void testStressSet() {		
    	final ExtObjectContainer container = db();
    	final int itemCount = 3000;
		for (int i=0; i<itemCount; ++i) {
    		Item item = new Item("item " + i);
    		container.set(item);
    		container.set(item);
    		container.commit();
    		container.set(item);
    		container.set(item);
    		container.commit();
    	}    	
    	for (int i=0; i<itemCount; ++i) {
    		String itemName = "item " + i;
    		final Item found = query(itemName);
    		Assert.isNotNull(found, "'" + itemName + "' not found");
			Assert.areEqual(itemName, found.name);
    	}
    }

}
