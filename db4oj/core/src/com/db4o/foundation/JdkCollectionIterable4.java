/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.foundation;

import java.util.*;

/**
 * @decaf.ignore.jdk11
 * @sharpen.ignore
 */
public class JdkCollectionIterable4 implements Iterable4{
    
    private final Collection _collection;
    
    public JdkCollectionIterable4(Collection collection){
        _collection = collection;
    }

    public Iterator4 iterator() {
        return new JdkCollectionIterator4(_collection);
    }

}
