/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


public class StringMarshaller1 extends StringMarshaller{
    
    public boolean inlinedStrings(){
        return true;
    }
    
    public int lengthInPayload(YapStream stream, Object obj) {
        if(obj == null){
            return 0;
        }
        return stream.alignToBlockSize( stream.stringIO().length((String)obj) );
    }
    
    public Object marshall(Object obj, YapWriter writer, boolean redirect) {
        
        YapStream stream = writer.getStream();
        String str = (String) obj;
        
        if(! redirect){
            if(str != null){
                writeShort(stream,str , writer);
            }
            // TODO:  Really we should return a YapWriter for indexing but
            //        for now it's not needed since this is used for untyped
            //        references only which are not indexed.
            return str;  
        }
        
        if (str == null) {
            writer.writeEmbeddedNull();
            return null;
        }
        
        int length = stream.stringIO().length(str);
        
        YapWriter bytes = new YapWriter(writer.getTransaction(), length);
        writeShort(stream, str, bytes);
        
        writer.writePayload(bytes);
        return bytes;
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
