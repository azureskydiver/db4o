/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;


/**
 * @exclude
 */
public class MultidimensionalArrayIterator4TestCase implements TestCase {
    
    public void testEmptyArray() {
        assertExhausted(new MultidimensionalArrayIterator4(new Object[0])); 
    }
    
    public void testArray() {
        MultidimensionalArrayIterator4 i = new MultidimensionalArrayIterator4(new Object[] { new Object[]{"foo", "bar"}, new Object[] {"fly"} });
        Assert.isTrue(i.moveNext());
        Assert.areEqual("foo", i.current());
        
        Assert.isTrue(i.moveNext());
        Assert.areEqual("bar", i.current());
        
        Assert.isTrue(i.moveNext());
        Assert.areEqual("fly", i.current());
        
        assertExhausted(i);
    }
    
    private void assertExhausted(final MultidimensionalArrayIterator4 i) {
        Assert.isFalse(i.moveNext());       
        Assert.expect(ArrayIndexOutOfBoundsException.class, new CodeBlock(){
            public void run() throws Throwable {
                System.out.println(i.current());
            }
        });
    }


}
