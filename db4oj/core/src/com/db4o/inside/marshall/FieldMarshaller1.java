		/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.btree.*;

/**
 * @exclude
 */
public class FieldMarshaller1 extends FieldMarshaller0 {
    
    private boolean hasBTreeIndex(YapField field){
        return ! field.isVirtual();
    }

    public void write(Transaction trans, YapClass clazz, YapField field, YapReader writer) {
        super.write(trans, clazz, field, writer);
        if(! hasBTreeIndex(field)){
            return;
        }
        writer.writeIDOf(trans, field.getIndex(trans));
    }

    public RawFieldSpec readSpec(YapStream stream, YapReader reader) {
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
    
    protected YapField fromSpec(RawFieldSpec spec, YapStream stream, YapField field) {
		YapField actualField = super.fromSpec(spec, stream, field);
		if(spec==null) {
			return field;
		}
		if (spec.indexID() != 0) {
			actualField.initIndex(stream.getSystemTransaction(), spec.indexID());
		}
		return actualField;
	}
    
    public int marshalledLength(YapStream stream, YapField field) {
        int len = super.marshalledLength(stream, field);
        if(! hasBTreeIndex(field)){
            return len;
        }
        final int BTREE_ID = YapConst.ID_LENGTH;
        return  len + BTREE_ID;
    }

    public void defrag(YapClass yapClass, YapField yapField, YapStringIO sio,
    		ReaderPair readers)
    		throws CorruptionException {
    	super.defrag(yapClass, yapField, sio, readers);
// TODO defrag field indices if present
//    	BTree index = yapField.getIndex(null);
//    	if(index!=null) {
//    		index.defragIndex(source, target, mapping);
//    	}
//    	else {
//    		source.incrementOffset(YapConst.INT_LENGTH);
//    		target.incrementOffset(YapConst.INT_LENGTH);
//    	}
    	readers.writeInt(0);
    }
}
