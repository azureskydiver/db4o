/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;

public class EmbeddedClientObjectContainerTestCase implements TestLifeCycle {

    private static final String FILENAME = "mtoc.db4o";
    
    private LocalObjectContainer _server;

    private EmbeddedClientObjectContainer _client1;

    private EmbeddedClientObjectContainer _client2;
    
    public static class Item{

        public String _name;

        public Item(String name) {
            _name = name;
        }
    }

    public void testSetAndCommitIsolation() {
        Item item = new Item("one");
        _client1.set(item);
        assertItemCount(_client2, 0);
        _client1.commit();
        assertItemCount(_client2, 1);
    }
    
    public void testStoredFieldIsolation(){
        Item storedItem = new Item("original");
        _client1.set(storedItem);
        _client1.commit();
        
        storedItem._name = "changed";
        _client1.set(storedItem);
        
        
        Query query = _client2.query();
        query.constrain(Item.class);
        ObjectSet objectSet = query.execute();
        Item retrievedItem = (Item) objectSet.next();
        
        StoredClass storedClass = _client2.storedClass(Item.class);
        StoredField storedField = storedClass.storedField("_name", null);
        Object retrievedName = storedField.get(retrievedItem);
        Assert.areEqual("original", retrievedName);
        
        _client1.commit();
        
        retrievedName = storedField.get(retrievedItem);
        Assert.areEqual("changed", retrievedName);
    }
    

    public void testClose() {
        final BooleanByRef closed = new BooleanByRef();
        
        // FIXME: Sharpen doesn't understand the null parameter (the third one), we had to add a cast
        //        to get sharpen to run through.
        
        Transaction trans = new LocalTransaction(_server, _server.systemTransaction(), (TransactionalReferenceSystem)null) {
            public void close(boolean rollbackOnClose) {
                super.close(rollbackOnClose);
                closed.value = true;
            }
        };
        EmbeddedClientObjectContainer client = new EmbeddedClientObjectContainer(_server, trans);
        // FIXME: close needs to unregister reference system
        //        also for crashed clients 
        client.close();
        Assert.isTrue(closed.value);
    }
    
    private void assertItemCount(EmbeddedClientObjectContainer client, int count) {
        Query query = client.query();
        query.constrain(Item.class);
        ObjectSet result = query.execute();
        Assert.areEqual(count, result.size());
    }

    public void setUp() throws Exception {
        File4.delete(FILENAME);
        Configuration config = Db4o.newConfiguration();
        
        // ExtObjectServer server = Db4o.openServer(config, FILENAME, 0);
        // EmbeddedClientObjectContainer container = server.openClient();
        
        _server = (LocalObjectContainer) Db4o.openFile(config, FILENAME);
        _client1 = new EmbeddedClientObjectContainer(_server);
        _client2 = new EmbeddedClientObjectContainer(_server);

    }

    public void tearDown() throws Exception {
        _client1.close();
        _client2.close();
        _server.close();
    }
    
}
