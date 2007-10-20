/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta;

import java.util.*;

import com.db4o.*;
import com.db4o.internal.handlers.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class DateTransparentActivationTestCase extends
        TransparentActivationTestCaseBase implements OptOutCS {

    public static Date first = new Date(107, 10, 19);

    public static void main(String[] args) {
        new DateTransparentActivationTestCase().runAll();
    }

    protected void store() throws Exception {
        TADateItem item = TADateItem.itemList(first, 10);
        store(item);
        
        TADateItemArray itemArray = TADateItemArray.itemArrayList(10);
        store(itemArray);
    }

    public void testDateItem() {
        TADateItem item = retrieveDateItemHeader();
        Date expected = first;
        
        for (int i = 0; i < 9; i++) {
            asertNullItem(item);
            Assert.areEqual(expected, item.getTyped());
            Assert.areEqual(expected, item._untyped);
            Assert.isNotNull(item.next());
            item = item.next();
            expected = new Date(expected.getTime() + (10 - i - 1) * TADateItem.DAY);
        }
        Assert.areEqual(expected, item.getTyped());
        Assert.areEqual(expected, item.getUntyped());
        Assert.isNull(item.next());
    }
    
    private void asertNullItem(TADateItem item) {
        Assert.areEqual(emptyValue(), item._typed);
        Assert.isNull(item._untyped);
        Assert.isNull(item._next);
    }
    
    private Object emptyValue() {
        return new DateHandler(null).primitiveNull();
    }

    private TADateItem retrieveDateItemHeader() {
        Query query = db().query();
        query.constrain(TADateItem.class);
        query.descend("_typed").constrain(first);
        ObjectSet result = query.execute();
        Assert.areEqual(1, result.size());
        return (TADateItem) result.next();
    }
    
    public void testDateItemArray() {
        TADateItemArray itemArray = retrieveDateItemArrayHeader();
        for (int i = 0; i < 9; i++) {
            assertNullItemArray(itemArray);
            assertArrayValues(TADateItemArray.data, itemArray.getTyped());
            assertArrayValues(TADateItemArray.data, itemArray.getUntyped());
            Assert.isNotNull(itemArray.next());
            itemArray = itemArray.next();
        }
    }
    
    private TADateItemArray retrieveDateItemArrayHeader() {
        Query query = db().query();
        query.constrain(TADateItemArray.class);
        query.descend("_depth").constrain(new Integer(10));
        ObjectSet result = query.execute();
        Assert.areEqual(1, result.size());
        return (TADateItemArray) result.next();
    }
    
    private void assertNullItemArray(TADateItemArray itemArray) {
        Assert.isNull(itemArray._typed);
        Assert.isNull(itemArray._untyped);
        Assert.isNull(itemArray._next);
        Assert.areEqual(0, itemArray._depth);
    }
    
    private void assertArrayValues(Date[] expected, Date[] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.areEqual(expected[i], actual[i]);
        }
    }
    
    private void assertArrayValues(Date[] expected, Object[] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            Assert.areEqual(expected[i], actual[i]);
        }
    }
}
