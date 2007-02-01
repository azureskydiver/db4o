/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;


public class StringMarshaller1 extends StringMarshaller{
	
    private static final int DEFRAGMENT_INCREMENT_OFFSET = YapConst.INT_LENGTH * 2;  
    
    public boolean inlinedStrings(){
        return true;
    }
    
    public void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection) {
        
        if(topLevel){
            header.addBaseLength(linkLength());
            header.prepareIndexedPayLoadEntry(trans);
        }else{
            if(withIndirection){
                header.addPayLoadLength(linkLength());
            }
        }
        
        if(obj == null){
            return;
        }
        
        header.addPayLoadLength(trans.stream().stringIO().length((String)obj));
    }
    
    public Object writeNew(Object obj, boolean topLevel, StatefulBuffer writer, boolean redirect) {
        
        ObjectContainerBase stream = writer.getStream();
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
        
        StatefulBuffer bytes = new StatefulBuffer(writer.getTransaction(), length);
        writeShort(stream, str, bytes);
        
        writer.writePayload(bytes, topLevel);
        return bytes;
    }
    
    public Buffer readIndexEntry(StatefulBuffer parentSlot) throws CorruptionException{
        int payLoadOffSet = parentSlot.readInt();
        int length = parentSlot.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return parentSlot.readPayloadWriter(payLoadOffSet, length);
    }
    
    public Buffer readSlotFromParentSlot(ObjectContainerBase stream, Buffer reader) throws CorruptionException {
        int payLoadOffSet = reader.readInt();
        int length = reader.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        return reader.readPayloadReader(payLoadOffSet, length);
    }

	public void defrag(SlotReader reader) {
		reader.incrementOffset(DEFRAGMENT_INCREMENT_OFFSET);
	}

}
