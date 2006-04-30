/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;

public abstract class ObjectMarshaller {
    
    public MarshallerFamily _family;

    protected YapWriter createWriter(Transaction a_trans, YapObject yo, int a_updateDepth) {
        int id = yo.getID();
        int address = -1;
        int length = objectLength(yo);
        if(! a_trans.i_stream.isClient()){
            address = a_trans.i_file.getSlot(length); 
        }
        a_trans.setPointer(id, address, length);
        return createWriter(a_trans, a_updateDepth, id, address, length);
    }

    protected YapWriter createWriter(Transaction a_trans, int a_updateDepth, int id, int address, int length) {
        YapWriter writer = new YapWriter(a_trans, length);
        writer.useSlot(id, address, length);
        if (Deploy.debug) {
            writer.writeBegin(YapConst.YAPOBJECT, length);
        }
        writer.setUpdateDepth(a_updateDepth);
        return writer;
    }
    
    public abstract boolean findOffset(YapClass yc, YapReader a_bytes, YapField a_field);
    
    public abstract void instantiateFields(YapClass yc, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes);
    
    protected abstract int headerLength();
    
    public abstract YapWriter marshallNew(Transaction a_trans, YapObject yo, int a_updateDepth);
    
    public abstract void marshallUpdate(
        Transaction a_trans,
        YapClass yapClass,
        int a_id,
        int a_updateDepth,
        YapObject a_yapObject,
        Object a_object
        );


    protected void marshallUpdateWrite(Transaction a_trans, YapClass yapClass, YapObject a_yapObject, Object a_object, YapWriter writer) {
        if (Deploy.debug) {
            writer.writeEnd();
            writer.debugCheckBytes();
        }
        YapStream stream = a_trans.i_stream;
        stream.writeUpdate(yapClass, writer);
        if (a_yapObject.isActive()) {
            a_yapObject.setStateClean();
        }
        a_yapObject.endProcessing();
        yapClass.dispatchEvent(stream, a_object, EventDispatcher.UPDATE);
    }
    
    protected int objectLength(YapObject yo){
        return headerLength() + memberLength(yo.getYapClass(), yo);
    }
    
    private int memberLength(YapClass yc, YapObject yo) {
        int length = YapConst.YAPINT_LENGTH;
        if (yc.i_ancestor != null) {
            length += memberLength(yc.i_ancestor, yo);
        }
        if (yc.i_fields != null) {
            for (int i = 0; i < yc.i_fields.length; i++) {
                length +=  fieldLength(yc.i_fields[i], yo);
            }
        }
        return length;
    }
    
    protected abstract int fieldLength(YapField yf, YapObject yo);
    
    

    

 
    

}