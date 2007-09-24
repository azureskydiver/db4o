/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.db4ounit.util.*;
import com.db4o.foundation.*;

import db4ounit.*;


/**
 * @exclude
 */
public class MultiDimensionalArrayHandlerUpdateTestCase extends HandlerUpdateTestCaseBase{
    
    // TODO: make asymmetrical once we support
    public static final int[][] intData2D = new int[][]{
        new int[]{
            1, 2, 3
        },
        new int[] {
            4, 5, 6
        }
    };
    
    // TODO: make asymmetrical once we support
    public static final String [][] stringData2D = new String [][]{
        new String[] {
            "one",
            "two",
        },
        new String [] {
            "three",
            "four",
        }
    };
    
    // TODO: make asymmetrical once we support
    public static final Object[][] objectData2D = new Object [][]{
        new Object []{
            new Item("one"),
            null,
            new Item("two"),
        },
        new Object []{
            new Item("three"),
            new Item("four"),
            null
        }
    };
    
    // TODO: make asymmetrical once we support
    public static final Object[][] stringObjectData2D = new Object [][]{
        new Object []{
            "one",
            "two",
        },
        new Object []{
            "three",
            "four",
        }
    };
    
    public static final byte [][] byteData2D = new byte [][]{
        ByteHandlerUpdateTestCase.data,
        ByteHandlerUpdateTestCase.data,
    };
    
    
    
    
    public static class ItemArrays {
        
        public int[][] _typedIntArray;
        
        public Object _untypedIntArray;
        
        public String[][] _typedStringArray;
        
        public Object _untypedStringArray;
        
        public Object[][] _objectArray;
        
        public Object[][] _stringObjectArray;
        
        public byte [][] _typedByteArray;
        
    }
    
    
    public static class Item {
        
        public String _name;
        
        public Item (String name){
            _name = name;
        }
        
        public boolean equals(Object obj) {
            
            if(! (obj instanceof Item)){
                return false;
            }
            
            Item other = (Item) obj;
            
            if(_name == null){
                return other._name == null;
            }
            
            return _name.equals(other._name);
        }
        
    }
    
    protected Object createArrays() {
        ItemArrays item = new ItemArrays();
        item._typedIntArray = intData2D;
        item._untypedIntArray = intData2D;
        item._typedStringArray = stringData2D;
        item._untypedStringArray = stringData2D;
        item._objectArray = objectData2D;
        item._stringObjectArray = stringObjectData2D;
        item._typedByteArray = byteData2D;
        return item;
    }
    
    
    protected void assertArrays(Object obj) {
        ItemArrays item = (ItemArrays) obj;
        assertAreEqual(intData2D, item._typedIntArray);
        assertAreEqual(intData2D, castToIntArray2D(item._untypedIntArray));
        assertAreEqual(stringData2D, item._typedStringArray);
        assertAreEqual(stringData2D, (String[][]) item._untypedStringArray);
        assertAreEqual(objectData2D, item._objectArray);
        assertAreEqual(objectData2D, item._objectArray);
        assertAreEqual(byteData2D, item._typedByteArray);
    }
    
    public static void assertAreEqual(int[][] expected, int[][] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            ArrayAssert.areEqual(expected[i], actual[i]);
        }
    }
    
    public static void assertAreEqual(String[][] expected, String[][] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            ArrayAssert.areEqual(expected[i], actual[i]);
        }
    }
    
    public static void assertAreEqual(Object[][] expected, Object[][] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            ArrayAssert.areEqual(expected[i], actual[i]);
        }
    }

    protected int[][] castToIntArray2D(Object obj){
        ObjectByRef byRef = new ObjectByRef(obj);
        correctIntArray2DJavaOnly(byRef);
        return (int[][]) byRef.value;
    }
    
    public static void assertAreEqual(byte[][] expected, byte[][] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            ArrayAssert.areEqual(expected[i], actual[i]);
        }
    }

    
    /**
     * @sharpen.remove
     */
    protected void correctIntArray2DJavaOnly(ObjectByRef byRef){
        if(_db4oHeaderVersion == VersionServices.HEADER_30_40){
            
            // Bug in the oldest format: 
            // It accidentally converted int[][] arrays to Integer[][] arrays.
            
            Integer[][] wrapperArray = (Integer[][])byRef.value;
            int[][] res = new int[wrapperArray.length][];
            for (int i = 0; i < wrapperArray.length; i++) {
                res[i] = castToIntArray(wrapperArray[i]);
            }
            byRef.value = res;
        }
    }
    

    protected Object[] createValues() {
        // not used
        return null;
    }

    protected void assertValues(Object[] values) {
        // not used
    }

    protected String typeName() {
        return "multidimensional_array";
    }
    
}
