/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * Regression test case for COR-1117
 */

public class CallbackTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new CallbackTestCase().runEmbeddedClientServer();
    }

    public void test() {
        Item item = new Item();
        store(item);
        db().commit();
        Assert.isTrue(item.isStored());
        Assert.isTrue(db().ext().isStored(item));

        ObjectSet result = retrieveItems();
        Assert.areEqual(1, result.size());

        Item retrievedItem = (Item) result.next();
        retrievedItem.save();

        result = retrieveItems();
        Assert.areEqual(1, result.size());
    }

    ObjectSet retrieveItems() {
        Query q = newQuery();
        q.constrain(Item.class);
        return q.execute();
    }

    public static class Item {
        public String test;

        public transient ObjectContainer _objectContainer;

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
