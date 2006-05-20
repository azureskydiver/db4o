/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;
import com.db4o.foundation.*;

public abstract class ObjectMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract void addFieldIndices(YapClass yc, ObjectHeaderAttributes attributes, YapWriter writer, boolean isNew) ;
    
    protected int alignedBaseLength(YapObject yo, ObjectHeaderAttributes attributes){
        int len = linkLength(yo.getYapClass(), yo, attributes) + headerLength();
        if(attributes != null){
            len += attributes.marshalledLength();
        }
        return yo.getStream().alignToBlockSize(len);
    }

    protected YapWriter createWriter(Transaction trans, YapObject yo, ObjectHeaderAttributes attributes, int updateDepth) {
        int id = yo.getID();
        int address = -1;
        int length = objectLength(yo, attributes);
        
        if(! trans.i_stream.isClient()){
            address = trans.i_file.getSlot(length); 
        }
        trans.setPointer(id, address, length);
        return createWriter(trans, yo, attributes, updateDepth, id, address, length);
    }

    protected YapWriter createWriter(Transaction a_trans, YapObject yo, ObjectHeaderAttributes attributes, int a_updateDepth, int id, int address, int length) {
        YapWriter writer = new YapWriter(a_trans, length);
        writer.useSlot(id, address, length);
        if (Deploy.debug) {
            writer.writeBegin(YapConst.YAPOBJECT, length);
        }
        writer.setUpdateDepth(a_updateDepth);
        writer._payloadOffset = alignedBaseLength(yo, attributes);
        return writer;
    }
    
    public abstract void deleteMembers(YapClass yc, ObjectHeaderAttributes attributes, YapWriter a_bytes, int a_type, boolean isUpdate);
    
    public abstract boolean findOffset(YapClass yc, ObjectHeaderAttributes attributes, YapReader a_bytes, YapField a_field);
    
    protected abstract int headerLength();
    
    public abstract void instantiateFields(YapClass yc, ObjectHeaderAttributes attributes, YapObject a_yapObject, Object a_onObject, YapWriter a_bytes);
    
    protected abstract int linkLength(YapClass yc, YapObject yo, ObjectHeaderAttributes attributes);

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
    
    protected abstract int objectLength(YapObject yo, ObjectHeaderAttributes attributes);
    
    public abstract Object readIndexEntry(YapClass yc, ObjectHeaderAttributes attributes, YapField yf, YapWriter reader);

    public abstract ObjectHeaderAttributes readHeaderAttributes(YapReader reader);
    
    public abstract void readVirtualAttributes(Transaction trans,  YapClass yc, YapObject yo, ObjectHeaderAttributes attributes, YapReader reader);
    

}