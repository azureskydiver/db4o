/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;

/**
 * 
 */
public class YapClassCollectionIterator extends Iterator4Impl{
    
    private final YapClassCollection i_collection;
    
    YapClassCollectionIterator(YapClassCollection a_collection, List4 a_first){
        super(a_first);
        i_collection = a_collection;
    }
    
    public boolean moveNext() {
    	if (super.moveNext()) {
    		i_collection.readYapClass(currentClass(), null);
    		return true;
    	}
    	return false;
    }
    
    public YapClass currentClass(){
        return (YapClass)current();
    }
}
