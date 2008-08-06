/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ArrayHandlerTestCase extends AbstractDb4oTestCase {
    
    public static void main(String[] args) {
        new ArrayHandlerTestCase().runSolo();
    }
    
    public static class IntArrayHolder{
        public int[] _ints;
        public IntArrayHolder(int[] ints){
            _ints = ints;
        }
    }
    
    public static class StringArrayHolder{
        public String[] _strings;
        public StringArrayHolder(String[] strings){
            _strings = strings;
        }
    }
    
    private ArrayHandler intArrayHandler(){
        return arrayHandler(int.class, true);
    }

    private ArrayHandler stringArrayHandler(){
        return arrayHandler(String.class, false);
    }
    
    private ArrayHandler arrayHandler(Class clazz, boolean isPrimitive) {
        ClassMetadata classMetadata = container().produceClassMetadata(reflector().forClass(clazz));
        return new ArrayHandler(classMetadata.typeHandler(), isPrimitive);
    }
    
    public void testIntArrayReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        int[] expected = new int[]{7, 8, 9};
        intArrayHandler().write(writeContext, expected);
        MockReadContext readContext = new MockReadContext(writeContext);
        int[] actual = (int[]) intArrayHandler().read(readContext);
        ArrayAssert.areEqual(expected, actual);
    }

    public void testIntArrayStoreObject() throws Exception{
        IntArrayHolder expectedItem = new IntArrayHolder(new int[] {1, 2, 3});
        db().store(expectedItem);
        db().purge(expectedItem);
        IntArrayHolder readItem = (IntArrayHolder) retrieveOnlyInstance(IntArrayHolder.class);
        Assert.areNotSame(expectedItem, readItem);
        ArrayAssert.areEqual(expectedItem._ints, readItem._ints);
    }
    
    public void testStringArrayReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        String[] expected = new String[]{"one", "two", "three"};
        stringArrayHandler().write(writeContext, expected);
        MockReadContext readContext = new MockReadContext(writeContext);
        String[] actual = (String[]) stringArrayHandler().read(readContext);
        ArrayAssert.areEqual(expected, actual);
    }

    public void testStringArrayStoreObject() throws Exception{
        StringArrayHolder expectedItem = new StringArrayHolder(new String[] {"one", "two", "three"});
        db().store(expectedItem);
        db().purge(expectedItem);
        StringArrayHolder readItem = (StringArrayHolder) retrieveOnlyInstance(StringArrayHolder.class);
        Assert.areNotSame(expectedItem, readItem);
        ArrayAssert.areEqual(expectedItem._strings, readItem._strings);
    }
    
    public void testHandlerVersion(){
        IntArrayHolder intArrayHolder = new IntArrayHolder(new int[0]);
        store(intArrayHolder);
        ReflectClass claxx = reflector().forObject(intArrayHolder);
        ClassMetadata classMetadata = (ClassMetadata) container().typeHandlerForReflectClass(claxx);
        FieldMetadata fieldMetadata = classMetadata.fieldMetadataForName("_ints");
        TypeHandler4 arrayHandler = fieldMetadata.getHandler();
        Assert.isInstanceOf(ArrayHandler.class, arrayHandler);
        assertCorrectedHandlerVersion(arrayHandler, 0, ArrayHandler0.class);
        assertCorrectedHandlerVersion(arrayHandler, 1, ArrayHandler2.class);
        assertCorrectedHandlerVersion(arrayHandler, 2, ArrayHandler2.class);
        if(NullableArrayHandling.enabled()){
            assertCorrectedHandlerVersion(arrayHandler, 3, ArrayHandler3.class);
        }
        assertCorrectedHandlerVersion(arrayHandler, HandlerRegistry.HANDLER_VERSION, ArrayHandler.class);
    }
    
    private void assertCorrectedHandlerVersion(TypeHandler4 arrayHandler, int version, Class handlerClass) {
        TypeHandler4 correctedHandlerVersion = container().handlers().correctHandlerVersion(arrayHandler, version);
        Assert.isInstanceOf(handlerClass, correctedHandlerVersion);
    }

}
