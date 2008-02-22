/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.foundation.*;

import db4ounit.*;

public class JdkCollectionIterator4TestCase implements TestCase{
    
    public static void main(String[] arguments) {
        new TestRunner(JdkCollectionIterator4TestCase.class).run();
    }
    
    public void test(){
        Collection collection = new ArrayList();
        Object[] content = new String[]{"one", "two", "three"};
        for (int i = 0; i < content.length; i++) {
            collection.add(content[i]);    
        }
        Iterator4 iterator = new JdkCollectionIterator4(collection);
        Iterator4Assert.areEqual(content, iterator); 
    }

}
