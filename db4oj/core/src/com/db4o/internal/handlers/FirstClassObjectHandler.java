/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class FirstClassObjectHandler implements TypeHandler4{
	
	private final ClassMetadata _classMetadata;

	public FirstClassObjectHandler(ClassMetadata classMetadata) {
		_classMetadata = classMetadata;
	}

	public void defragment(DefragmentContext context) {
		if(_classMetadata.hasClassIndex()) {
			context.copyID();
		}
		else {
			context.copyUnindexedID();
		}
		int restLength = (_classMetadata.linkLength()-Const4.INT_LENGTH);
		context.incrementOffset(restLength);
		
	}

	public void delete(DeleteContext context) throws Db4oIOException {
    	((ObjectContainerBase)context.objectContainer()).deleteByID(
    			context.transaction(), context.readInt(), context.cascadeDeleteDepth());
	}

	public Object read(ReadContext context) {
		
        // FIXME: .NET value types should get their own TypeHandler and it 
        //        should do the following:
        if(_classMetadata.isValueType()){
            ActivationDepth activationDepth = ((UnmarshallingContext)context).activationDepth();
			return _classMetadata.readValueType(context.transaction(), context.readInt(), activationDepth.descend(_classMetadata));
        }
        
        return context.readObject();
	}

	public void write(WriteContext context, Object obj) {
		context.writeObject(obj);
	}

	public PreparedComparison prepareComparison(Object source) {
		if(source == null){
			return new PreparedComparison() {
				public int compareTo(Object obj) {
					if(obj == null){
						return 0;
					}
					return -1;
				}
			
			};
		}
		int id = 0;
		ReflectClass claxx = null;
        if(source instanceof Integer){
        	id = ((Integer)source).intValue();
        } else if(source instanceof TransactionContext){
            TransactionContext tc = (TransactionContext)source;
            Object obj = tc._object;
            id = _classMetadata.stream().getID(tc._transaction, obj);
            claxx = _classMetadata.reflector().forObject(obj);
        }else{
        	throw new IllegalComparisonException();
        }
    	return new ClassMetadata.PreparedComparisonImpl(id, claxx);
	}

}
