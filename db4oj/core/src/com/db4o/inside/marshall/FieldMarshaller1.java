/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;

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

    public YapField read(YapStream stream, YapField originalField, YapReader reader) {
        YapField actualField = super.read(stream, originalField, reader);
        if(! hasBTreeIndex(actualField)){
            return actualField;
        }
        int id = reader.readInt();
        if(id == 0){
            return actualField;
        }
        actualField.initIndex(stream.getSystemTransaction(), id);
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

}
