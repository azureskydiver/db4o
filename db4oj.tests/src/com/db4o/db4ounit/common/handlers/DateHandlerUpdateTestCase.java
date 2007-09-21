/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import java.util.*;

import db4ounit.*;

public class DateHandlerUpdateTestCase extends HandlerUpdateTestCaseBase {
    
    public static class Item {
        
        public Date _date;

        public Object _untyped;
    }

    public static class ItemArrays {

        public Date[] _dateArray;

        public Object[] _untypedObjectArray;

        public Object _arrayInObject;

    }

    private static final Date[] data = { new Date(Long.MIN_VALUE),
            new Date(Long.MIN_VALUE + 1), new Date(-1), new Date(0),
            new Date(1), new Date(Long.MAX_VALUE - 1),
            new Date(Long.MAX_VALUE), };

    public static void main(String[] args) {
        new TestRunner(DateHandlerUpdateTestCase.class).run();
    }

    protected void assertArrays(Object obj) {
        ItemArrays itemArrays = (ItemArrays) obj;
        Date[] dateArray = (Date[]) itemArrays._arrayInObject;
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], itemArrays._dateArray[i]);
            assertAreEqual(data[i], (Date) itemArrays._untypedObjectArray[i]);
            assertAreEqual(data[i], dateArray[i]);
        }
        
        // FIXME: We are not signalling null for Dates in typed arrays in 
        //        the current handler format:
        
        // Assert.isNull(itemArrays._dateArray[data.length]);
        
        Assert.isNull(itemArrays._untypedObjectArray[data.length]);
        Assert.isNull(dateArray[data.length]);
    }

    protected void assertValues(Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._date);
            assertAreEqual(data[i], (Date)item._untyped);
        }

        Item nullItem = (Item) values[values.length - 1];
        Assert.isNull(nullItem._date);
        Assert.isNull(nullItem._untyped);
    }

    private void assertAreEqual(Date expected, Date actual) {
        if(expected.equals(new Date(Long.MAX_VALUE)) && _handlerVersion == 0){
            // Bug in the oldest format: It treats a Long.MAX_VALUE date as null. 
            expected = null;
        }
        Assert.areEqual(expected, actual);
    }

    protected Object createArrays() {
        ItemArrays itemArrays = new ItemArrays();
        itemArrays._dateArray = new Date[data.length + 1];
        System.arraycopy(data, 0, itemArrays._dateArray, 0, data.length);

        itemArrays._untypedObjectArray = new Object[data.length + 1];
        System.arraycopy(data, 0, itemArrays._untypedObjectArray, 0,
                data.length);

        Date[] dateArray = new Date[data.length + 1];
        System.arraycopy(data, 0, dateArray, 0, data.length);
        itemArrays._arrayInObject = dateArray;
        return itemArrays;
    }

    protected Object[] createValues() {
        Item[] values = new Item[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            Item item = new Item();
            item._date = data[i];
            item._untyped = data[i];
            values[i] = item;
        }
        values[values.length - 1] = new Item();
        return values;
    }

    protected String typeName() {
        return "date";
    }

}
