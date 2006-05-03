/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


public class StringMarshaller0 extends StringMarshaller{
    
    public boolean inlinedStrings(){
        return false;
    }
    
    public Object marshall(Object a_object, YapWriter a_bytes) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        
        YapStream stream = a_bytes.getStream();
        String str = (String) a_object;
        int length = stream.stringIO().length(str);
        
        YapWriter bytes = new YapWriter(a_bytes.getTransaction(), length);
        
        writeShort(stream, str, bytes);
        
        bytes.setID(a_bytes._offset);
        
        a_bytes.getStream().writeEmbedded(a_bytes, bytes);
        a_bytes.incrementOffset(YapConst.YAPID_LENGTH);
        a_bytes.writeInt(length);
        return bytes;
    }
    
    public int marshalledLength(YapStream stream, Object obj) {
        return 0;
    }
    
    public YapWriter readIndexEntry(YapWriter parentSlot) throws CorruptionException{
        return parentSlot.getStream().readObjectWriterByAddress(parentSlot.getTransaction(), parentSlot.readInt(), parentSlot.readInt());
    }
    
    public YapReader readSlotFromParentSlot(YapStream stream, YapReader reader) throws CorruptionException {
        return reader.readEmbeddedObject(stream.getTransaction());
    }




}
