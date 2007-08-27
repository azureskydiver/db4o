/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.FloatHandler;

import db4ounit.Assert;

public class FloatHandlerTestCase extends TypeHandlerTestCaseBase {
    
    public static void main(String[] args) {
        new FloatHandlerTestCase().runSolo();
    }

    private FloatHandler floatHandler() {
        return new FloatHandler(stream());
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Float expected = new Float(Float.MAX_VALUE);
        floatHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        Float f = (Float) floatHandler().read(readContext);
        
        Assert.areEqual(expected, f);
    }
    
    public void testStoreObject() {
        Item storedItem = new Item(1.23456789f, new Float(1.23456789f));
        doTestStoreObject(storedItem);
    }
    
    public static class Item extends TypeHandlerTestCaseBase.Item {
        public float _float;
        public Float _floatWrapper;
        public Item(float f, Float wrapper) {
            _float = f;
            _floatWrapper = wrapper;
        }
        public boolean equals(Object obj) {
            if(obj == this){
                return true;
            }
            if (!(obj instanceof Item)) {
                return false;
            }
            Item other = (Item)obj;
            return (other._float == this._float) 
                    && this._floatWrapper.equals(other._floatWrapper);
        }
        
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + new Float(_float).hashCode();
            hash = 31 * hash + (null == _floatWrapper ? 0 : _floatWrapper.hashCode());
            return hash;
        }
        
        public String toString() {
            return "[" + _float + ","+ _floatWrapper + "]";
        }
    }
}
