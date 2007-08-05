/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre11.internal;

import com.db4o.db4ounit.common.internal.*;

import db4ounit.*;


/**
 * @exclude
 */
public class EmbeddedClientObjectContainerJre11TestCase extends EmbeddedClientObjectContainerTestCase{
    
    public void testQuery(){
        Item storedItem = storeItemToClient1AndCommit();
        Object retrievedItem = _client1.query(Item.class).next();
        Assert.areSame(storedItem, retrievedItem);
        
        retrievedItem = _client1.query(new ItemPredicate()).next();
        Assert.areSame(storedItem, retrievedItem);
        
        retrievedItem = _client1.query(new ItemPredicate(), null).next();
        Assert.areSame(storedItem, retrievedItem);
    }

}
