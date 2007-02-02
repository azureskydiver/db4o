/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside.query.processor;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.marshall.*;


class NullFieldMetadata extends FieldMetadata{
    
    public NullFieldMetadata(){
        super(null);
    }
    
    public Comparable4 prepareComparison(Object obj){
		return Null.INSTANCE;
	}
	
	Object read(MarshallerFamily mf,  StatefulBuffer a_bytes) {
		return null;
	}
	
	public Object readQuery(Transaction a_trans, MarshallerFamily mf, Buffer a_reader) throws CorruptionException {
		return null;
	}
}
