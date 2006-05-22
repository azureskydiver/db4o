/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


public class StringMarshaller1 extends StringMarshaller{
    
    public boolean inlinedStrings(){
        return true;
    }
    
    public Object marshall(Object obj, YapWriter writer) {
        if (obj == null) {
            writer.writeEmbeddedNull();
            return null;
        }
        
        YapStream stream = writer.getStream();
        String str = (String) obj;
        int length = stream.stringIO().length(str);
        
        YapWriter bytes = new YapWriter(writer.getTransaction(), length);
        writeShort(stream, str, bytes);
        writer.writePayload(bytes);
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
