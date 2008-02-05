/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;

import db4ounit.*;


public class ArrayList4Asserter {
    
    public static void assertTrimToSize_EnsureCapacity(final ArrayList4<Integer> list) throws Exception {
        list.ensureCapacity(ListAsserter.CAPACITY*2);
        checkTrimToSize_EnsureCapacity(list);
        list.trimToSize();
        checkTrimToSize_EnsureCapacity(list);
    }

    public static void checkTrimToSize_EnsureCapacity(final ArrayList4<Integer> list) {
        Assert.areEqual(ListAsserter.CAPACITY, list.size());
        for(int i = 0; i < ListAsserter.CAPACITY; ++i) {
            Integer element = list.get(i);
            Assert.areEqual(new Integer(i), element);
        }
    }

    public static void assertEnsureCapacity_Iterator(final ArrayList4<Integer> list) throws Exception {
    	final Iterator<Integer> iterator = list.iterator();
    	list.ensureCapacity(ListAsserter.CAPACITY*2);
    	Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
    		public void run() throws Throwable {
    			iterator.next();
    		}
    	});
    }

    public static void assertTrimToSize_Iterator(final ArrayList4<Integer> list) throws Exception {
    	final Iterator<Integer> iterator = list.iterator();
    	list.trimToSize();
    	Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
    		public void run() throws Throwable {
    			iterator.next();
    		}
    	});
    }

}
