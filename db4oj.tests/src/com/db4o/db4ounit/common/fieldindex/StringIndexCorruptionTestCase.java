/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.internal.*;

import db4ounit.Assert;

/**
 * Jira ticket: COR-373
 * 
 * @exclude
 */
public class StringIndexCorruptionTestCase extends StringIndexTestCaseBase {
	
	public static void main(String[] arguments) {
		new StringIndexCorruptionTestCase().runSolo();
	}
	
	protected void configure(Configuration config) {
		super.configure(config);
		config.bTreeNodeSize(4);
	    config.flushFileBuffers(false); // this just make the test faster
	}
	
	public void testStressSet() {		
    	final ExtObjectContainer container = db();
    	
    	final int itemCount = 300;
		for (int i=0; i<itemCount; ++i) {
    		Item item = new Item(itemName(i));
    		container.set(item);
    		container.set(item);
    		container.commit();
    		container.set(item);
    		container.set(item);
    		container.commit();
    	}    	
    	for (int i=0; i<itemCount; ++i) {
    		String itemName = itemName(i);
    		final Item found = query(itemName);
    		Assert.isNotNull(found, "'" + itemName + "' not found");
			Assert.areEqual(itemName, found.name);
    	}
    }
	
	private String itemName(int i) {
		return "item " + i;
	}

}
