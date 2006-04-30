/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


public class StringMarshaller0 {
    
    public Object marshall(YapString handler, Object a_object, YapWriter a_bytes) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        String str = (String) a_object;
        int length = handler.i_stringIo.length(str);
        YapWriter bytes = new YapWriter(a_bytes.getTransaction(), length);
        if (Deploy.debug) {
            bytes.writeBegin(YapConst.YAPSTRING, length);
        }
        bytes.writeInt(str.length());
        handler.i_stringIo.write(bytes, str);
        if (Deploy.debug) {
            bytes.writeEnd();
        }
        bytes.setID(a_bytes._offset);
        
        a_bytes.getStream().writeEmbedded(a_bytes, bytes);
        a_bytes.incrementOffset(YapConst.YAPID_LENGTH);
        a_bytes.writeInt(length);
        return bytes;
    }


}
