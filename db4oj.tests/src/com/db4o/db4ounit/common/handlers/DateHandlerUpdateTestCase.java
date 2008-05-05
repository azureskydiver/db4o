/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import java.util.*;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;

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

    private static final Date[] data = {
		new Date(DatePlatform.MIN_DATE),
        new Date(DatePlatform.MIN_DATE + 1),
        new Date(1191972104500L),
        new Date(DatePlatform.MAX_DATE - 1),
        new Date(DatePlatform.MAX_DATE),
    };

    public static void main(String[] args) {
        new ConsoleTestRunner(DateHandlerUpdateTestCase.class).run();
    }

    protected void assertArrays(Object obj) {
        ItemArrays itemArrays = (ItemArrays) obj;
        Date[] dateArray = (Date[]) itemArrays._arrayInObject;
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], itemArrays._dateArray[i]);
            assertAreEqual(data[i], (Date) itemArrays._untypedObjectArray[i]);
            assertAreEqual(data[i], dateArray[i]);
        }
        
        
        // Assert.isNull(itemArrays._dateArray[data.length]);
        
        Assert.isNull(itemArrays._untypedObjectArray[data.length]);
        
        // FIXME: We are not signalling null for Dates in typed arrays in 
        //        the current handler format:        
        Assert.areEqual(emptyValue(), dateArray[data.length]);
    }

    protected void assertValues(Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._date);
            assertAreEqual(data[i], (Date)item._untyped);
        }
        
        Item emptyItem = (Item) values[values.length - 1];
        Assert.areEqual(emptyValue(), emptyItem._date);
        Assert.isNull(emptyItem._untyped);
    }

	private Object emptyValue() {
		return Platform4.reflectorForType(Date.class).forClass(Date.class).nullValue();
	}

    private void assertAreEqual(Date expected, Date actual) {
        if(expected.equals(new Date(DatePlatform.MAX_DATE)) && db4oHandlerVersion() == 0){
            // Bug in the oldest format: It treats a Long.MAX_VALUE date as null. 
            expected = MarshallingConstants0.NULL_DATE;
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
