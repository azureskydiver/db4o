/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class ObjectMarshaller2Spike extends ObjectMarshaller1 {
    
    public ObjectMarshaller2Spike() {
        if(! MarshallingSpike.enabled){
            throw new IllegalStateException();
        }
    }
    
    public StatefulBuffer marshallNew(Transaction trans, ObjectReference ref, int updateDepth){
        
        MarshallingContext context = new MarshallingContext(trans, ref, updateDepth, true);
        
        marshall(ref.getObject(), context);
        
        return context.ToWriteBuffer();
    }
    
    protected void marshall(final Object obj, final MarshallingContext context) {
        
        final Transaction trans = context.transaction();

        TraverseFieldCommand command = new TraverseFieldCommand() {
            
            private int fieldIndex = -1; 
            
            public int fieldCount(ClassMetadata classMetadata, Buffer buffer) {
                int fieldCount = classMetadata.i_fields.length;
                
                context.fieldCount(fieldCount);
                
                return fieldCount;
            }
            
            public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                fieldIndex++;
                Object child = field.getOrCreate(trans, obj);
                if(child == null) {
                    context.isNull(fieldIndex, true);
                    field.addIndexEntry(trans, context.objectID(), null);
                    return;
                }
                
                if (child instanceof Db4oTypeImpl) {
                    child = ((Db4oTypeImpl) child).storedTo(trans);
                }
                field.marshall(context, child);
            }
        };
        
        // traverseFields(context.classMetadata(), writer, context, command);
        
        traverseFields(context.classMetadata(), null, context, command);
    }

}
