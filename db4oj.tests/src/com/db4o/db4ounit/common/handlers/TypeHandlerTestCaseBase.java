/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;


import db4ounit.Assert;
import db4ounit.extensions.*;

public class TypeHandlerTestCaseBase extends AbstractDb4oTestCase {
	
	abstract public static class Item {
	
	}
	
	protected void doTestStoreObject(Item storedItem){
        db().set(storedItem);
        db().purge(storedItem);
    
        Item readItem = (Item) retrieveOnlyInstance(Item.class);
        
        Assert.areNotSame(storedItem, readItem);
        Assert.areEqual((Object)storedItem, (Object)readItem);
		
	}

}