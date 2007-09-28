/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.marshall.*;

/**
 * @exclude
 */
public class NullFieldMetadata extends FieldMetadata {
    
    public NullFieldMetadata(){
        super(null);
    }
    
    public Comparable4 prepareComparison(Object obj){
		return Null.INSTANCE;
	}
	
	public final Object read(InternalReadContext context) {
	    return null;
	}
	
}
