		/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.io.IOException;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;

/**
 * @exclude
 */
public class FieldMarshaller1 extends FieldMarshaller0 {
    
    private boolean hasBTreeIndex(FieldMetadata field){
        return ! field.isVirtual();
    }

    public void write(Transaction trans, ClassMetadata clazz, FieldMetadata field, ByteArrayBuffer writer) {
        super.write(trans, clazz, field, writer);
        if(! hasBTreeIndex(field)){
            return;
        }
        writer.writeIDOf(trans, field.getIndex(trans));
    }

    public RawFieldSpec readSpec(ObjectContainerBase stream, ByteArrayBuffer reader) {
    	RawFieldSpec spec=super.readSpec(stream, reader);
    	if(spec==null) {
    		return null;
    	}
		if (spec.isVirtual()) {
			return spec;
		}
        int indexID = reader.readInt();
        spec.indexID(indexID);
    	return spec;
    }
    
    protected FieldMetadata fromSpec(RawFieldSpec spec, ObjectContainerBase stream, FieldMetadata field) {
		FieldMetadata actualField = super.fromSpec(spec, stream, field);
		if(spec==null) {
			return field;
		}
		if (spec.indexID() != 0) {
			actualField.initIndex(stream.systemTransaction(), spec.indexID());
		}
		return actualField;
	}
    
    public int marshalledLength(ObjectContainerBase stream, FieldMetadata field) {
        int len = super.marshalledLength(stream, field);
        if(! hasBTreeIndex(field)){
            return len;
        }
        final int BTREE_ID = Const4.ID_LENGTH;
        return  len + BTREE_ID;
    }

    public void defrag(ClassMetadata yapClass, FieldMetadata yapField, LatinStringIO sio,
    		final DefragmentContextImpl context)
    		throws CorruptionException, IOException {
    	super.defrag(yapClass, yapField, sio, context);
    	if(yapField.isVirtual()) {
    		return;
    	}
    	if(yapField.hasIndex()) {
        	BTree index = yapField.getIndex(context.systemTrans());
    		int targetIndexID=context.copyID();
    		if(targetIndexID!=0) {
    			index.defragBTree(context.services());
    		}
    	}
    	else {
        	context.writeInt(0);
    	}
    }
}
