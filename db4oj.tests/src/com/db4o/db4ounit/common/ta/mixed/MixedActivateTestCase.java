/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.activation.*;
import com.db4o.db4ounit.common.ta.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;

public class MixedActivateTestCase extends ItemTestCaseBase {

    public static void main(String[] args) {
        new MixedActivateTestCase().runAll();
    }

    protected void assertItemValue(Object obj) throws Exception {

    }

    protected void assertRetrievedItem(Object obj) throws Exception {
        Item item = (Item) obj;

        for (int i = 0; i < 10; i++) {
            assertNullItem(item, 10 - i);
            item = item.next();
        }
    }

    private void assertNullItem(Item item, int i) {
        if (i % 2 == 0) {
            Assert.isNull(item._name);
            Assert.isNull(item._next);
            Assert.areEqual(0, item._value);
        } else {
            Assert.areEqual("Item " + i, item._name);
            Assert.areEqual(i, item._value);
            Assert.isNotNull(item._next);
        }
    }

    protected Object createItem() throws Exception {
        TAItem item = TAItem.newTAITem(10);
        item._isRoot = true;

        return item;
    }

    public Object retrieveOnlyInstance(Class clazz) {
        Query q = db().query();
        q.constrain(clazz);
        q.descend("_isRoot").constrain(new Boolean(true));
        return q.execute().next();
    }

    public static class Item {

        public String _name;

        public int _value;

        public Item _next;

        public boolean _isRoot;

        public static Item newItem(int depth) {
            if (depth == 0) {
                return null;
            }
            Item header = new Item();
            header._name = "Item " + depth;
            header._value = depth;
            header._next = TAItem.newTAITem(depth - 1);
            return header;
        }

        public String getName() {
            return _name;
        }

        public int getValue() {
            return _value;
        }

        public Item next() {
            return _next;
        }

        public String toString() {
            return _name;
        }
    }

    public static class TAItem extends Item implements Activatable {

        private transient Activator _activator;

        public static TAItem newTAITem(int depth) {
            if (depth == 0) {
                return null;
            }
            TAItem header = new TAItem();
            header._name = "TAItem " + depth;
            header._value = depth;
            header._next = Item.newItem(depth - 1);
            return header;
        }

        public String getName() {
            activate();
            return _name;
        }

        public int getValue() {
            activate();
            return _value;
        }

        public Item next() {
            activate();
            return _next;
        }

        public String toString() {
            activate();
            return _name;
        }

        public void activate() {
            if (_activator == null)
                return;
            _activator.activate();
        }

        public void bind(Activator activator) {
            if (null != _activator) {
                throw new IllegalStateException();
            }
            _activator = activator;
        }
    }

}
