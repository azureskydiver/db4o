/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import db4ounit.*;

public class IntHandlerUpdateTestCase extends HandlerUpdateTestCaseBase {
    
    private static final int[] data = new int[] { 
        Integer.MIN_VALUE, 
        Integer.MIN_VALUE + 1,
        -5,
        -1,
        0,
        1,
        5,
        Integer.MAX_VALUE - 1,
        Integer.MAX_VALUE
    };

    public static void main(String[] args) {
        new TestRunner(IntHandlerUpdateTestCase.class).run();
    }
    
    protected String typeName() {
        return "int";
    }
    
    public static class Item {
        
        public int _typedPrimitive;
        
        public Integer _typedWrapper;
        
        public Object _untyped;
        
    }
    
    public static class ItemArrays {
        
        public int[] _typedPrimitiveArray;
        
        public Integer[] _typedWrapperArray;
        
        public Object[] _untypedObjectArray;
        
        public Object _primitiveArrayInObject;
        
        public Object _wrapperArrayInObject;
        
    }
    
    protected Object[] createValues() {
        Item[] values = new Item[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            Item item = new Item();
            values[i] = item;
            item._typedPrimitive = data[i];
            item._typedWrapper = new Integer(data[i]);
            item._untyped = new Integer(data[i]);
        }
        values[values.length - 1] = new Item();
        return values;
    }
    
    protected Object createArrays() {
        ItemArrays item = new ItemArrays();
        createTypedPrimitiveArray(item);
        createTypedWrapperArray(item);
        
        // Will be removed for .NET by sharpen.
        createUntypedObjectArray(item);
        
        createPrimitiveArrayInObject(item);
        createWrapperArrayInObject(item);
        return item;
    }
    
    /**
     * @sharpen.remove
     */
    private void createUntypedObjectArray(ItemArrays item){
        item._untypedObjectArray = new Integer[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            item._untypedObjectArray[i] = new Integer(data[i]);
        }
    }
    
    private void createTypedPrimitiveArray(ItemArrays item){
        item._typedPrimitiveArray = new int[data.length];
        System.arraycopy(data, 0, item._typedPrimitiveArray, 0, data.length);
    }
    
    private void createTypedWrapperArray(ItemArrays item){
        item._typedWrapperArray = new Integer[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            item._typedWrapperArray[i] = new Integer(data[i]);
        }
    }
    
    private void createPrimitiveArrayInObject(ItemArrays item){
        int[] arr = new int[data.length];
        System.arraycopy(data, 0, arr, 0, data.length);
        item._primitiveArrayInObject = arr;
    }
    
    private void createWrapperArrayInObject(ItemArrays item){
        Integer[] arr = new Integer[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            arr[i] = new Integer(data[i]);
        }
        item._wrapperArrayInObject = arr;
    }

    protected void assertValues(Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._typedPrimitive);
            assertAreEqual(new Integer(data[i]), item._typedWrapper);
            assertAreEqual( new Integer(data[i]), item._untyped);
        }
        Item nullItem = (Item) values[values.length - 1];
        Assert.areEqual(0, nullItem._typedPrimitive);
        Assert.isNull(nullItem._typedWrapper);
        Assert.isNull(nullItem._untyped);
    }
    
    protected void assertArrays(Object obj) {
        ItemArrays item = (ItemArrays) obj;
        assertTypedPrimitiveArray(item);
        assertTypedWrapperArray(item);
        
        // Will be removed for .NET by sharpen.
        assertUntypedObjectArray(item);
        
        assertPrimitiveArrayInObject(item);
        assertWrapperArrayInObject(item);
    }    
    
    private void assertTypedPrimitiveArray(ItemArrays item) {
        assertData(item._typedPrimitiveArray);
    }

    private void assertTypedWrapperArray(ItemArrays item) {
        assertWrapperData(item._typedWrapperArray);
    }

    /**
     * @sharpen.remove
     */
    protected void assertUntypedObjectArray(ItemArrays item) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Integer(data[i]), item._untypedObjectArray[i]);
        }
        Assert.isNull(item._untypedObjectArray[item._untypedObjectArray.length - 1]);
    }
    
    private void assertPrimitiveArrayInObject(ItemArrays item) {
        if(_db4oHeaderVersion == HEADER_30_40){
           // Bug in the oldest format: It accidentally int[] arrays to Integer[] arrays.
            assertWrapperData((Integer[]) item._primitiveArrayInObject);
        } else{
            assertData((int[]) item._primitiveArrayInObject);
        }
    }
    
    private void assertWrapperArrayInObject(ItemArrays item) {
        assertWrapperData((Integer[]) item._wrapperArrayInObject);
    }


    private void assertData(int[] values) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], values[i]);
        }
    }

    private void assertWrapperData(Integer[] values) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Integer(data[i]), values[i]);
        }
        
        // FIXME: The following fails as is because of a deficiency 
        //        in the storage format of arrays.
        
        //        Arrays should also get a null Bitmap to fix.
        
        // Assert.isNull(values[values.length - 1]);
    }
    
    private void assertAreEqual(int expected, int actual){
        if(expected == Integer.MAX_VALUE  && _handlerVersion == 0){
            // Bug in the oldest format: It treats Integer.MAX_VALUE as null. 
            expected = 0;
        }
        Assert.areEqual(expected, actual);
    }
    
    private void assertAreEqual(Object expected, Object actual){
        if(new Integer(Integer.MAX_VALUE).equals(expected) && _handlerVersion == 0){
            // Bug in the oldest format: It treats Integer.MAX_VALUE as null.
            expected = null;
        }
        Assert.areEqual(expected, actual);
    }

}
