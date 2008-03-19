/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;

import db4ounit.*;


public class MultiDimensionalArrayHandlerTestCase extends TypeHandlerTestCaseBase {

    public static void main(String[] args) {
        new MultiDimensionalArrayHandlerTestCase().runSolo();
    }
    
    public static class Item{
        
        public int [][] _int;
        
        public Item(int[][] int_){
            _int = int_;
        }
        
        public boolean equals(Object obj) {
            if(obj == this){
                return true;
            }
            if (!(obj instanceof Item)) {
                return false;
            }
            Item other = (Item)obj;
            
            if(_int.length != other._int.length){
                return false;
            }
            
            for (int i = 0; i < _int.length; i++) {
                if(_int[i].length != other._int[i].length){
                    return false;
                }
                for (int j = 0; j < _int[i].length; j++) {
                    if(_int[i][j] != other._int[i][j]){
                        return false;
                    }
                }
            }
            return true;
        }
        
    }
    
    private ArrayHandler intArrayHandler(){
        return arrayHandler(int.class, true);
    }

//    private ArrayHandler stringArrayHandler(){
//        return arrayHandler(String.class, false);
//    }
    
    private ArrayHandler arrayHandler(Class clazz, boolean isPrimitive) {
        TypeHandler4 typeHandler = (TypeHandler4) stream().fieldHandlerForClass(reflector().forClass(clazz));
        return new MultidimensionalArrayHandler(typeHandler, isPrimitive);
    }
    
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Item expected = new Item(new int[][]{new int[]{1, 2, 3}, new int[]{6,5,4}});
        intArrayHandler().write(writeContext, expected._int);
        
        MockReadContext readContext = new MockReadContext(writeContext);
        
        
        int[][] arr = (int[][])intArrayHandler().read(readContext);
        Item actualValue = new Item(arr);
        Assert.areEqual(expected, actualValue);
    }
    
    public void testStoreObject() throws Exception{
        Item storedItem = new Item(new int[][]{new int[]{1, 2, 3}, new int[]{6,5,4}});
        doTestStoreObject(storedItem);
    }

}
