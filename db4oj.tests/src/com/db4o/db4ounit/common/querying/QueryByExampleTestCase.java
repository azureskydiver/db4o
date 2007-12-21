/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class QueryByExampleTestCase extends AbstractDb4oTestCase {

    static final int COUNT = 100;

    static LinkedList list = LinkedList.newLongCircularList();

    public static void main(String[] args) {
        new QueryByExampleTestCase().runSolo();
    }

    protected void store() {
        store(list);
    }

    public void testByExample() {
        Query q = db().query();
        q.constrain(list).byExample();
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());
    }

    public void testByIdentity() {
        Query q = db().query();

        q.constrain(LinkedList.class);
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());
        while (result.hasNext()) {
            db().delete(result.next());
        }

        q = db().query();
        q.constrain(LinkedList.class);
        result = q.execute();
        Assert.areEqual(0, result.size());

        LinkedList newList = LinkedList.newLongCircularList();
        db().set(newList);
        q = db().query();
        q.constrain(newList);
        result = q.execute();
        Assert.areEqual(1, result.size());

    }

    public void testClassConstraint() {
        Query q = db().query();
        q.constrain(LinkedList.class);
        ObjectSet result = q.execute();
        Assert.areEqual(COUNT, result.size());

        q = db().query();
        q.constrain(LinkedList.class).byExample();
        result = q.execute();
        Assert.areEqual(100, result.size());

    }

    public static class LinkedList {

        public LinkedList _next;

        public transient int _depth;

        public static LinkedList newLongCircularList() {
            LinkedList head = new LinkedList();
            LinkedList tail = head;
            for (int i = 1; i < COUNT; i++) {
                tail._next = new LinkedList();
                tail = tail._next;
                tail._depth = i;
            }
            tail._next = head;
            return head;
        }

        public String toString() {
            return "List[" + _depth + "]";
        }
    }

}
