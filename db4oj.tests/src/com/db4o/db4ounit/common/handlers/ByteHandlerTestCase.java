/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.handlers.ByteHandler;

import db4ounit.Assert;

public class ByteHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new ByteHandlerTestCase().runSolo();
    }
    
    public static class Item extends TypeHandlerTestCaseBase.Item {
        
        public byte _byte;
        
        public Byte _byteWrapper;
        
        public Item(byte b, Byte wrapper){
            _byte = b;
            _byteWrapper = wrapper;
        }
        
        public boolean equals(Object obj) {
        	if(obj == this){
        		return true;
        	}
        	if (!(obj instanceof Item)) {
        		return false;
			}
        	Item other = (Item)obj;
        	return (other._byte == this._byte) 
        			&& this._byteWrapper.equals(other._byteWrapper);
        	        	
        }
        
        public int hashCode() {
        	int hash = 7;
        	hash = 31 * hash + _byte;
        	hash = 31 * hash + (null == _byteWrapper ? 0 : _byteWrapper.hashCode());
        	return hash;
        }
        public String toString() {
    		return "[" + _byte + "," + _byteWrapper + "]";
        }
        
    }
    
    private ByteHandler byteHandler() {
        return new ByteHandler(stream());
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Byte expected = new Byte((byte)0x61);
        byteHandler().write(writeContext, expected);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        
        Byte byteValue = (Byte)byteHandler().read(readContext);
        Assert.areEqual(expected, byteValue);
    }
    
    public void testStoreObject() throws Exception{
        Item storedItem = new Item((byte)5, new Byte((byte)6));
        doTestStoreObject(storedItem);
    }
}
