/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;

/**
 * Regression test case for COR-1117
 */

public class CallbackTestCase implements TestLifeCycle {

    public static void main(String[] args) {
        new TestRunner(CallbackTestCase.class).run();
    }

    ObjectServer _server;

    ObjectContainer _client;

    String dbfilename = "cor1117.yap"; //$NON-NLS-1$

    public void setUp() throws Exception {
        _server = Db4o.openServer(dbfilename, 0);
        _client = _server.openClient();
    }

    public void tearDown() throws Exception {
        _client.close();
        _server.close();
        File dbFile = new File(dbfilename);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public void test() {
        Item item = new Item();
        _client.store(item);
        _client.commit();
        Assert.isTrue(item.isStored());
        Assert.isTrue(_client.ext().isStored(item));

        ObjectSet result = retrieveItems();
        Assert.areEqual(1, result.size());

        Item retrievedItem = (Item) result.next();
        retrievedItem.save();

        result = retrieveItems();
        Assert.areEqual(1, result.size());
    }

    ObjectSet retrieveItems() {
        Query q = _client.query();
        q.constrain(Item.class);
        return q.execute();
    }

    public static class Item {
        String test;

        transient ObjectContainer _objectContainer;

        public void objectOnNew(ObjectContainer container) {
            _objectContainer = container;
        }

        public boolean isStored() {
            return _objectContainer.ext().isStored(this);
        }

        public void save() {
            _objectContainer.store(this);
            _objectContainer.commit();
        }
    }
}
