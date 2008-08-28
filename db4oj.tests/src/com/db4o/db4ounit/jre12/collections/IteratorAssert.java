/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import db4ounit.*;


/**
 * @decaf.ignore.jdk11
 */
public class IteratorAssert {
    
    public static void areEqual(Iterator expected, Iterator actual) {
        if (null == expected) {
            Assert.isNull(actual);
            return;
        }
        Assert.isNotNull(actual);       
        while (expected.hasNext()) {
            Assert.isTrue(actual.hasNext());
            Assert.areEqual(expected.next(), actual.next());
        }
        Assert.isFalse(actual.hasNext());
    }

    public static void areEqual(Object[] expected, Iterator iterator) {
        Vector v = new Vector();
        for (int i = 0; i < expected.length; i++) {
            v.add(expected[i]);
        }
        areEqual(v.iterator(), iterator);
    }

}
