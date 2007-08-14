/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class ObjectMarshallerSpike extends ObjectMarshaller1 {
    
    public ObjectMarshallerSpike() {
        if(! MarshallingSpike.enabled){
            throw new IllegalStateException();
        }
    }
    
    public StatefulBuffer marshallNew(Transaction trans, ObjectReference ref, int updateDepth){
        
        MarshallingContext context = new MarshallingContext(trans, ref, updateDepth, true);
        
        StatefulBuffer writer = null;
        
        marshall(ref, ref.getObject(), context, writer);
        
        return context.ToWriteBuffer();
    }
    
    protected void marshall(final ObjectReference ref, final Object obj, final MarshallingContext context, final StatefulBuffer writer) {
        
        final Transaction trans = context.transaction();

        TraverseFieldCommand command = new TraverseFieldCommand() {
            
            private int fieldIndex = -1; 
            
            public int fieldCount(ClassMetadata yapClass, Buffer reader) {
                int fieldCount = yapClass.i_fields.length;
                reader.writeInt(fieldCount);
                return fieldCount;
            }
            
            public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
                fieldIndex++;
                Object child = field.getOrCreate(trans, obj);
                if(child == null) {
                    context.isNull(fieldIndex, true);
                    field.addIndexEntry(trans, writer.getID(), null);
                    return;
                }
                
                if (child instanceof Db4oTypeImpl) {
                    child = ((Db4oTypeImpl) child).storedTo(trans);
                }
                field.marshall(ref, child, _family, writer, containingClass.configOrAncestorConfig(), context.isNew());
            }
        };
        traverseFields(context.classMetadata(), writer, context, command);
    }

}
