/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.LongHandler;

import db4ounit.Assert;

public class LongHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new LongHandlerTestCase().runSolo();
    }
    
    private LongHandler longHandler() {
        return new LongHandler(stream());
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Long expected = new Long(0x1020304050607080l);
        longHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        Long longValue = (Long) longHandler().read(readContext);

        Assert.areEqual(expected, longValue);
    }
    
    public void testStoreObject() {
        Item storedItem = new Item(0x1020304050607080l, new Long(0x1122334455667788l));
        doTestStoreObject(storedItem);
    }
    
    public static class Item extends TypeHandlerTestCaseBase.Item {
        public long _long;
        public Long _longWrapper;
        public Item(long l, Long wrapper) {
            _long = l;
            _longWrapper = wrapper;
        }
        public boolean equals(Object obj) {
            if(obj == this){
                return true;
            }
            if (!(obj instanceof Item)) {
                return false;
            }
            Item other = (Item)obj;
            return (other._long == this._long) 
                    && this._longWrapper.equals(other._longWrapper);
        }
        
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + new Long(_long).hashCode();
            hash = 31 * hash + (null == _longWrapper ? 0 : _longWrapper.hashCode());
            return hash;
        }
        
        public String toString() {
            return "[" + _long + ","+ _longWrapper + "]";
        }
    }
    
}
