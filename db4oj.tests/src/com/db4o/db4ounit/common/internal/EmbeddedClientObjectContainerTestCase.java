/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.types.*;

import db4ounit.*;

public class EmbeddedClientObjectContainerTestCase implements TestLifeCycle {

    private static final String FIELD_NAME = "_name";

    private static final String FILENAME = "mtoc.db4o";
    
    private LocalObjectContainer _server;

    protected EmbeddedClientObjectContainer _client1;

    protected EmbeddedClientObjectContainer _client2;

    private static final String ORIGINAL_NAME = "original";
    
    private static final String CHANGED_NAME = "changed";


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
    
    public void testBackup(){
        Assert.expect(NotSupportedException.class, new CodeBlock() {
            public void run() throws Throwable {
                _client1.backup("");
            }
        });
    }
    
    public void testGetID(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        Assert.isGreater(1, id);
    }
    
    public void testGetByID(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        Assert.areSame(storedItem, _client1.getByID(id));
    }
    
    public void testBindIsolation(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        
        Item retrievedItem = retrieveItemFromClient2();
        
        Item boundItem = new Item(CHANGED_NAME);
        _client1.bind(boundItem, id);
        Assert.areSame(boundItem, _client1.getByID(id));
        Assert.areSame(retrievedItem, _client2.getByID(id));
    }
    
    public void testConfigure(){
        Assert.expect(NotSupportedException.class, new CodeBlock() {
            public void run() throws Throwable {
                _client1.configure();
            }
        });
    }
    
    public void testDescendIsolation(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.set(storedItem);
        
        Item retrievedItem = retrieveItemFromClient2();
        Object descendValue = _client1.descend(storedItem, new String[]{FIELD_NAME});
        Assert.areEqual(ORIGINAL_NAME, descendValue);
        
        _client1.commit();
        
        descendValue = _client1.descend(storedItem, new String[]{FIELD_NAME});
        Assert.areEqual(CHANGED_NAME, descendValue);
    }
    
    public void testGetObjectInfo(){
        Item storedItem = storeItemToClient1AndCommit();
        ObjectInfo objectInfo = _client1.getObjectInfo(storedItem);
        Assert.isNotNull(objectInfo);
    }
    
    public void testGetByUUID(){
        Item storedItem = storeItemToClient1AndCommit();
        ObjectInfo objectInfo = _client1.getObjectInfo(storedItem);
        
        Object retrievedItem = _client1.getByUUID(objectInfo.getUUID());
        Assert.areSame(storedItem, retrievedItem);
        
        retrievedItem = _client2.getByUUID(objectInfo.getUUID());
        Assert.areNotSame(storedItem, retrievedItem);
    }
    
    public void testIdenity(){
        Db4oDatabase identity1 = _client1.identity();
        Assert.isNotNull(identity1);
        Db4oDatabase identity2 = _client2.identity();
        Assert.isNotNull(identity2);
        
        // TODO: Db4oDatabase is shared between embedded clients.
        // This should work, since there is an automatic bind
        // replacement. Replication test cases will tell.
        Assert.areSame(identity1, identity2);
    }
    
    public void testKnownClasses(){
        ReflectClass[] knownClasses = _client1.knownClasses();
        ReflectClass itemClass = _client1.reflector().forClass(Item.class);
        arrayAssertContains(knownClasses, new ReflectClass[]{itemClass});
    }
    
    public void testLock(){
        Assert.areSame(_server.lock(), _client1.lock());
    }
    
    public void testPeekPersisted(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.set(storedItem);
        
        Item peekedItem = (Item) _client1.peekPersisted(storedItem, 2, true);
        Assert.isNotNull(peekedItem);
        Assert.areNotSame(peekedItem, storedItem);
        Assert.areEqual(ORIGINAL_NAME, peekedItem._name);
        
        peekedItem = (Item) _client1.peekPersisted(storedItem, 2, false);
        Assert.isNotNull(peekedItem);
        Assert.areNotSame(peekedItem, storedItem);
        Assert.areEqual(CHANGED_NAME, peekedItem._name);
        
        Item retrievedItem = retrieveItemFromClient2();
        peekedItem = (Item) _client2.peekPersisted(retrievedItem, 2, false);
        Assert.isNotNull(peekedItem);
        Assert.areNotSame(peekedItem, retrievedItem);
        Assert.areEqual(ORIGINAL_NAME, peekedItem._name);
    }
    
    public void testPurge(){
        Item storedItem = storeItemToClient1AndCommit();
        Assert.isTrue(_client1.isStored(storedItem));
        _client1.purge(storedItem);
        Assert.isFalse(_client1.isStored(storedItem));
    }
    
    public void testReflector(){
        Assert.isNotNull(_client1.reflector());
    }
    
    public void testActivate(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        
        Item retrievedItem = (Item) _client2.getByID(id);
        Assert.isNull(retrievedItem._name);
        Assert.isFalse(_client2.isActive(retrievedItem));
        
        _client2.activate(retrievedItem, 1);
        Assert.areEqual(ORIGINAL_NAME, retrievedItem._name);
        Assert.isTrue(_client2.isActive(retrievedItem));
    }
    
    public void testIsCached(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        
        Assert.isFalse(_client2.isCached(id));
        
        Item retrievedItem = (Item) _client2.getByID(id);
        Assert.isTrue(_client2.isCached(id));
    }
    
    public void testIsClosed(){
        _client1.close();
        Assert.isTrue(_client1.isClosed());
    }
    
    public void testIsStored(){
        Item storedItem = storeItemToClient1AndCommit();
        Assert.isTrue(_client1.isStored(storedItem));
        Assert.isFalse(_client2.isStored(storedItem));
    }
    
    public void testStoredFieldIsolation(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.set(storedItem);
        
        Item retrievedItem = retrieveItemFromClient2();
        
        StoredClass storedClass = _client2.storedClass(Item.class);
        StoredField storedField = storedClass.storedField(FIELD_NAME, null);
        Object retrievedName = storedField.get(retrievedItem);
        Assert.areEqual(ORIGINAL_NAME, retrievedName);
        
        _client1.commit();
        
        retrievedName = storedField.get(retrievedItem);
        Assert.areEqual(CHANGED_NAME, retrievedName);
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
    
    private Item storeItemToClient1AndCommit() {
        Item storedItem = new Item(ORIGINAL_NAME);
        _client1.set(storedItem);
        _client1.commit();
        return storedItem;
    }

    private Item retrieveItemFromClient2() {
        Query query = _client2.query();
        query.constrain(Item.class);
        ObjectSet objectSet = query.execute();
        Item retrievedItem = (Item) objectSet.next();
        return retrievedItem;
    }

    public void setUp() throws Exception {
        File4.delete(FILENAME);
        Configuration config = Db4o.newConfiguration();
        config.objectClass(Item.class).generateUUIDs(true);
        
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
    
    public static void arrayAssertContains(Object[] array, Object[] expected){
        for (int i = 0; i < expected.length; i++) {
            if (-1 == indexOf(array, expected[i])) {
                Assert.fail("Expecting contains '" + expected[i] + "'.");
            }
        }
    }
    
    public static int indexOf(Object[] array, Object expected) {
        for (int i = 0; i < array.length; ++i) {                
            if (expected.equals(array[i])) {
                return i;
            }
        }
        return -1;
    }


    
}
