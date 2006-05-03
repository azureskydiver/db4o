/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


public class StringMarshaller1 extends StringMarshaller{
    
    public boolean inlinedStrings(){
        return true;
    }
    
    public Object marshall(Object a_object, YapWriter a_bytes) {
        if (a_object == null) {
            a_bytes.writeEmbeddedNull();
            return null;
        }
        
        YapStream stream = a_bytes.getStream();
        String str = (String) a_object;
        int length = stream.stringIO().length(str);
        
        a_bytes.writeInt(a_bytes._payloadOffset);
        a_bytes.writeInt(length);
        
        YapWriter bytes = new YapWriter(a_bytes.getTransaction(), length);
        writeShort(stream, str, bytes);
        a_bytes.writePayload(bytes);
        return bytes;
    }
    
    public int marshalledLength(YapStream stream, Object obj) {
        if(obj == null){
            return 0;
        }
        return stream.alignToBlockSize( stream.stringIO().length((String)obj) );
    }
    
    public YapWriter readIndexEntry(YapWriter parentSlot) throws CorruptionException{
        int payLoadOffSet = parentSlot.readInt();
        int length = parentSlot.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return parentSlot.readPayloadWriter(payLoadOffSet, length);
    }
    
    public YapReader readSlotFromParentSlot(YapStream stream, YapReader reader) throws CorruptionException {
        int payLoadOffSet = reader.readInt();
        int length = reader.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return reader.readPayloadReader(payLoadOffSet, length);
    }



}
