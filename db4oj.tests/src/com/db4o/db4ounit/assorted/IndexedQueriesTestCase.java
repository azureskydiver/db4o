/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.assorted;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.*;

import db4ounit.extensions.*;

public class IndexedQueriesTestCase extends Db4oTestCase{
    
    public static void main(String[] arguments) {
        new IndexedQueriesTestCase().runSolo();
    }
    
    public static class IndexedQueriesItem{
        
        public String _name;
        
        public int _int;
        
        public Integer _integer;
        
        public IndexedQueriesItem() {
        }

        public IndexedQueriesItem(String name) {
            _name = name;
        }

        public IndexedQueriesItem(int int_) {
            _int = int_;
            _integer = new Integer(int_);
        }

    }

    public void configure() {
        indexField("_name");
        indexField("_int");
        indexField("_integer");
    }
    
    private void indexField(String fieldName){
        indexField(IndexedQueriesItem.class, fieldName);
    }
    

    public void store() {
        
        String[] strings = new String[] {"a", "c", "b", "f", "e"};
        
        for (int i = 0; i < strings.length; i++) {
            db().set(new IndexedQueriesItem(strings[i]));
        }
        
        int[] ints = new int[] {1, 5, 7, 3, 2, 3};
        
        for (int i = 0; i < ints.length; i++) {
            db().set(new IndexedQueriesItem(ints[i]));
        }

        assertQuery(1, "b");
        assertInts(5);
        assertIntegers();
    }

    public void test() throws Exception {
        
        assertNullNameCount(6);
        
        db().set(new IndexedQueriesItem("d"));
        assertQuery(1, "b");
        
        updateB();
        
        db().set(new IndexedQueriesItem("z"));
        db().set(new IndexedQueriesItem("y"));
        
        reopen();
        assertQuery(1, "b");

        assertInts(8);
    }
    
    private void assertIntegers(){
        
        Query q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_integer").constrain(new Integer(4)).greater().equal();
        assertIntsFound(new int[] { 5, 7 }, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_integer").constrain(new Integer(4)).smaller();
        assertIntsFound(new int[] { 1, 2, 3, 3 }, q);
        
    }

    private void assertInts(int expectedZeroSize) {
        
        Query q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(0));
        int zeroSize = q.execute().size();
        Test.ensure(zeroSize == expectedZeroSize);
        
        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(4)).greater().equal();
        assertIntsFound(new int[] { 5, 7 }, q);
         
        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(4)).greater();
        assertIntsFound(new int[] { 5, 7 }, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(3)).greater();
        assertIntsFound(new int[] { 5, 7 }, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(3)).greater().equal();
        assertIntsFound(new int[] { 3, 3, 5, 7 }, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(2)).greater().equal();
        assertIntsFound(new int[] { 2, 3, 3, 5, 7 }, q);
        q = Test.query();

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(2)).greater();
        assertIntsFound(new int[] { 3, 3, 5, 7 }, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(1)).greater().equal();
        assertIntsFound(new int[] { 1, 2, 3, 3, 5, 7 }, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(1)).greater();
        assertIntsFound(new int[] { 2, 3, 3, 5, 7 }, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(4)).smaller();
        assertIntsFound(new int[] { 1, 2, 3, 3 }, expectedZeroSize, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(4)).smaller().equal();
        assertIntsFound(new int[] { 1, 2, 3, 3 }, expectedZeroSize, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(3)).smaller();
        assertIntsFound(new int[] { 1, 2 }, expectedZeroSize, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(3)).smaller().equal();
        assertIntsFound(new int[] { 1, 2, 3, 3 }, expectedZeroSize, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(2)).smaller().equal();
        assertIntsFound(new int[] { 1, 2 }, expectedZeroSize, q);
        q = Test.query();

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(2)).smaller();
        assertIntsFound(new int[] { 1 }, expectedZeroSize, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(1)).smaller().equal();
        assertIntsFound(new int[] { 1 }, expectedZeroSize, q);

        q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_int").constrain(new Integer(1)).smaller();
        assertIntsFound(new int[] {}, expectedZeroSize, q);

    }

    private void assertIntsFound(int[] ints, int zeroSize, Query q) {
        ObjectSet res = q.execute();
        Test.ensure(res.size() == (ints.length + zeroSize));
        while (res.hasNext()) {
            IndexedQueriesItem ci = (IndexedQueriesItem)res.next();
            for (int i = 0; i < ints.length; i++) {
                if (ints[i] == ci._int) {
                    ints[i] = 0;
                    break;
                }
            }
        }
        for (int i = 0; i < ints.length; i++) {
            Test.ensure(ints[i] == 0);
        }
    }

    private void assertIntsFound(int[] ints, Query q) {
        assertIntsFound(ints, 0, q);
    }

    private void assertQuery(int count, String string) {
        ObjectSet res = queryForName(string);
        Test.ensure(res.size() == count);
        IndexedQueriesItem ci = (IndexedQueriesItem)res.next();
        Test.ensure(ci._name.equals("b"));
    }
    
    private void assertNullNameCount(int count) {
        ObjectSet res = queryForName(null);
        Test.ensure(res.size() == count);
        while(res.hasNext()){
            IndexedQueriesItem ci = (IndexedQueriesItem)res.next();
            Test.ensure(ci._name == null);
        }
    }

    private void updateB() {
        ObjectSet res = queryForName("b");
        IndexedQueriesItem ci = (IndexedQueriesItem)res.next();
        ci._name = "j";
        Test.objectContainer().set(ci);
        res = queryForName("b");
        Test.ensure(res.size() == 0);
        res = queryForName("j");
        Test.ensure(res.size() == 1);
        ci._name = "b";
        Test.objectContainer().set(ci);
        assertQuery(1, "b");
    }

    private ObjectSet queryForName(String n) {
        Query q = Test.query();
        q.constrain(IndexedQueriesItem.class);
        q.descend("_name").constrain(n);
        return q.execute();
    }

}
