/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.inside.marshall.*;
import com.db4o.inside.slots.*;

/**
 * @exclude
 */
public class YapFieldVersion extends YapFieldVirtual {

    YapFieldVersion(YapStream stream) {
        super();
        i_name = VirtualField.VERSION;
        i_handler = new YLong(stream);
    }
    
    public void addFieldIndex(MarshallerFamily mf, YapClass yapClass, YapWriter writer, Slot oldSlot) {
        writer.writeLong(writer.getStream().generateTimeStampId());
    }
    
    public void delete(MarshallerFamily mf, YapWriter a_bytes, boolean isUpdate) {
        a_bytes.incrementOffset(linkLength());
    }

    void instantiate1(Transaction a_trans, YapObject a_yapObject, YapReader a_bytes) {
        a_yapObject.virtualAttributes().i_version = a_bytes.readLong();
    }

    void marshall1(YapObject a_yapObject, YapWriter a_bytes, boolean a_migrating, boolean a_new) {
        YapStream stream = a_bytes.getStream().i_parent;
        VirtualAttributes va = a_yapObject.virtualAttributes();
        if (! a_migrating) {
            va.i_version = stream.generateTimeStampId();
        }
        if(va == null){
            a_bytes.writeLong(0);
        }else{
            a_bytes.writeLong(va.i_version);
        }
    }

    public int linkLength() {
        return YapConst.LONG_LENGTH;
    }
    
    void marshallIgnore(YapReader writer) {
        writer.writeLong(0);
    }


}