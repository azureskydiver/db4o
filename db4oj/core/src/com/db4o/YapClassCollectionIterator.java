/* Copyright (C) 2004 - 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;

/**
 * @exclude
 */
public class YapClassCollectionIterator extends MappingIterator {
    
    private final YapClassCollection i_collection;
    
    YapClassCollectionIterator(YapClassCollection a_collection, Iterator4 iterator){
        super(iterator);
        i_collection = a_collection;
    }
    
    public YapClass currentClass() {
        return (YapClass)current();
    }
    
	protected Object map(Object current) {
		return i_collection.readYapClass((YapClass)current, null);
	}
}
