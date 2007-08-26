/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;

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
        TypeHandler4 typeHandler = stream().handlers().handlerForClass(stream(), reflector().forClass(clazz));
        return new ArrayHandler(stream(),typeHandler, isPrimitive);
    }
    
    public void _testIntArrayReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        int[] expected = new int[]{7, 8, 9};
        intArrayHandler().write(writeContext, expected);
        MockReadContext readContext = new MockReadContext(writeContext);
        int[] actual = (int[]) intArrayHandler().read(readContext);
        ArrayAssert.areEqual(expected, actual);
    }

    public void _testIntArrayStoreObject() throws Exception{
        IntArrayHolder expectedItem = new IntArrayHolder(new int[] {1, 2, 3});
        db().set(expectedItem);
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

    public void _testStringArrayStoreObject() throws Exception{
        StringArrayHolder expectedItem = new StringArrayHolder(new String[] {"one", "two", "three"});
        db().set(expectedItem);
        db().purge(expectedItem);
        StringArrayHolder readItem = (StringArrayHolder) retrieveOnlyInstance(StringArrayHolder.class);
        Assert.areNotSame(expectedItem, readItem);
        ArrayAssert.areEqual(expectedItem._strings, readItem._strings);
    }


}
