/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre11.handlers;

import java.util.Date;

import com.db4o.db4ounit.common.handlers.*;
import com.db4o.internal.handlers.DateHandler;

import db4ounit.Assert;

public class DateHandlerTestCase extends TypeHandlerTestCaseBase {
    
    public static void main(String[] args) {
        new DateHandlerTestCase().runSolo();
    }
    
    private DateHandler dateHandler() {
        return new DateHandler();
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Date expected = new Date();
        dateHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        Date d =  (Date)dateHandler().read(readContext);
        
        Assert.areEqual(expected, d);
    }
    
    public void testStoreObject() {
        Item storedItem = new Item(new Date());
        doTestStoreObject(storedItem);
    }
    
    public static class Item {
        public Date date;
        public Item(Date date_) {
            this.date = date_;
        }
        public boolean equals(Object obj) {
            if(obj == this){
                return true;
            }
            if (!(obj instanceof Item)) {
                return false;
            }
            Item other = (Item)obj;
            return (date == null) ? (other.date == null) : (date.equals(other.date));
        }
        
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + (null == date ? 0 : date.hashCode());
            return hash;
        }
        
        public String toString() {
            return "[" + date + "]";
        }
    }
}
