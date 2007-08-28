/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;


public class UntypedHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new UntypedHandlerTestCase().runSolo();
    }
    
    public static class Item extends TypeHandlerTestCaseBase.Item {
        
        public Object _member;
        
        public Item(Object member) {
            _member = member;
        }
        
        public boolean equals(Object obj) {
            if(obj == this){
                return true;
            }
            if (!(obj instanceof Item)) {
                return false;
            }
            Item other = (Item)obj;
            return this._member.equals(other._member);
        }
        
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + (null == _member ? 0 : _member.hashCode());
            return hash;
        }
        
        public String toString() {
            return "[" + _member + "]";
        }
    }
    
    public void testStoreIntItem() throws Exception{
        doTestStoreObject(new Item(new Integer(3355)));
    }
    
    public void testStoreStringItem() throws Exception{
        doTestStoreObject(new Item("one"));
    }
    
    public void _testStoreArrayItem() throws Exception{
        doTestStoreObject(new Item(new String[]{"one", "two", "three"}));
    }
    


}
