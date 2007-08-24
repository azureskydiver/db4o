/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.internal.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 * @exclude
 */
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
    
    private ClassMetadata classHandler(Class clazz) {
        ReflectClass claxx = reflector().forClass(clazz);
        return stream().container().produceClassMetadata(claxx);
    }
    
    public void testIntArrayReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        IntArrayHolder expectedItem = new IntArrayHolder(new int[] {1, 2, 3});
        classHandler(IntArrayHolder.class).write(writeContext, expectedItem);
        MockReadContext readContext = new MockReadContext(writeContext);
        IntArrayHolder readItem = (IntArrayHolder) classHandler(IntArrayHolder.class).read(readContext);
        ArrayAssert.areEqual(expectedItem._ints, readItem._ints);
    }

    public void testIntArrayStoreObject() throws Exception{
        IntArrayHolder expectedItem = new IntArrayHolder(new int[] {1, 2, 3});
        db().set(expectedItem);
        db().purge(expectedItem);
        IntArrayHolder readItem = (IntArrayHolder) retrieveOnlyInstance(IntArrayHolder.class);
        Assert.areNotSame(expectedItem, readItem);
        ArrayAssert.areEqual(expectedItem._ints, readItem._ints);
    }


}
