/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class VersionFieldMetadata extends VirtualFieldMetadata {

    VersionFieldMetadata(ObjectContainerBase stream) {
        super(Handlers4.LONG_ID, new LongHandler(stream));
        setName(VirtualField.VERSION);
    }
    
    public void addFieldIndex(MarshallerFamily mf, ClassMetadata yapClass, StatefulBuffer writer, Slot oldSlot) {
        writer.writeLong(writer.getStream().generateTimeStampId());
    }
    
    public void delete(MarshallerFamily mf, StatefulBuffer a_bytes, boolean isUpdate) {
        a_bytes.incrementOffset(linkLength());
    }

    void instantiate1(Transaction a_trans, ObjectReference a_yapObject, SlotBuffer a_bytes) {
        a_yapObject.virtualAttributes().i_version = a_bytes.readLong();
    }

    void marshall(Transaction trans, ObjectReference ref, WriteBuffer buffer, boolean isMigrating, boolean isNew) {
        VirtualAttributes attr = ref.virtualAttributes();
        if (! isMigrating) {
            attr.i_version = trans.container()._parent.generateTimeStampId();
        }
        if(attr == null){
            buffer.writeLong(0);
        }else{
            buffer.writeLong(attr.i_version);
        }
    }

    protected int linkLength() {
        return Const4.LONG_LENGTH;
    }
    
    void marshallIgnore(WriteBuffer buffer) {
        buffer.writeLong(0);
    }


}