/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.ta;

import com.db4o.db4ounit.common.ta.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class TARefreshTestCase extends TransparentActivationTestCaseBase
        implements OptOutSolo {

    public static void main(String[] args) {
        new TARefreshTestCase().runClientServer();
    }
    
    private static final int ITEM_DEPTH = 10;

    private Class _class;
    protected void store() throws Exception {
        TAItem item = TAItem.newTAItem(ITEM_DEPTH);
        item._isRoot = true;
        _class = item.getClass();
        store(item);
    }
    
    public void testRefresh() {
        ExtObjectContainer client1 = openNewClient();
        ExtObjectContainer client2 = openNewClient();
        TAItem item1 = (TAItem) retrieveInstance(client1);
        TAItem item2 = (TAItem) retrieveInstance(client2);

        TAItem next1 = item1;
        int value = 10;
        while (next1 != null) {
            Assert.areEqual(value, next1.getValue());
            next1 = next1.next();
            value --;
        }
        
        TAItem next2 = item2;
        value = 10;
        while (next2 != null) {
            Assert.areEqual(value, next2.getValue());
            next2 = next2.next();
            value --;
        }
        
        //update depth = 1
        item1.setValue(100);
        item1.next().setValue(200);
        client1.set(item1);
        client1.commit();
        
        Assert.areEqual(100, item1.getValue());
        Assert.areEqual(200, item1.next().getValue());
        
        Assert.areEqual(10, item2.getValue());
        Assert.areEqual(9, item2.next().getValue());
        
        //refresh 0
        client2.refresh(item2, 0);
        Assert.areEqual(10, item2.getValue());
        Assert.areEqual(9, item2.next().getValue());
        
        //refresh 1
        client2.refresh(item2, 1);
        Assert.areEqual(100, item2.getValue());
        Assert.areEqual(9, item2.next().getValue());
        
        //refresh 2
        client2.refresh(item2, 2);
        Assert.areEqual(100, item2.getValue());
        //FIXME: maybe a bug
        //Assert.areEqual(200, item2.next().getValue());
        
        next1 = item1;
        value = 1000;
        while (next1 != null) {
            next1.setValue(value);
            next1 = next1.next();
            value++;
        }
        client1.set(item1);
        client1.commit();
        
        client2.refresh(item2, 5);
        next2 = item2;
        for (int i = 1000; i < 1005; i++) {
            Assert.areEqual(i, next2.getValue());
            next2 = next2.next();
        }
    }

    private Object retrieveInstance(ExtObjectContainer client) {
        Query query = client.query();
        query.constrain(_class);
        query.descend("_isRoot").constrain(new Boolean(true));
        return query.execute().next();
    }
    
    private ExtObjectContainer openNewClient() {
        return ((Db4oClientServerFixture) fixture()).openNewClient();
    }
    
    public static class TAItem extends ActivatableImpl {

        public int _value;

        public TAItem _next;

        public boolean _isRoot;

        public static TAItem newTAItem(int depth) {
            if (depth == 0) {
                return null;
            }
            TAItem root = new TAItem();
            root._value = depth;
            root._next = newTAItem(depth - 1);
            return root;
        }

        public int getValue() {
            activate();
            return _value;
        }

        public void setValue(int value) {
            activate();
            _value = value;
        }
        
        public TAItem next() {
            activate();
            return _next;
        }
    }
}
