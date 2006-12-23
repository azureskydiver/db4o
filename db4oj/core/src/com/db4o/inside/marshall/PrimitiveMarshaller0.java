/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import java.util.Date;

import com.db4o.*;


public class PrimitiveMarshaller0 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return true;
    }
    
    public int writeNew(Transaction trans, YapClassPrimitive yapClassPrimitive, Object obj, boolean topLevel, YapWriter parentWriter, boolean withIndirection, boolean restoreLinkOffset){
        
        int id = 0;
        
        if(obj != null){
            
            TypeHandler4 handler = yapClassPrimitive.i_handler;
        
            YapStream stream = trans.stream();
            id = stream.newUserObject();
            int address = -1;
            int length = objectLength(handler);
            if(! stream.isClient()){
                address = trans.i_file.getSlot(length); 
            }
            trans.setPointer(id, address, length);
            
            YapWriter writer = new YapWriter(trans, length);
            writer.useSlot(id, address, length);
            if (Deploy.debug) {
                writer.writeBegin(YapConst.YAPOBJECT);
            }
            writer.writeInt(yapClassPrimitive.getID());
            
            handler.writeNew(_family, obj, false, writer, true, false);
            
            writer.writeEnd();
            stream.writeNew(yapClassPrimitive, writer);
        }
        
        if(parentWriter != null){
            parentWriter.writeInt(id);
        }
        
        return id;
    }
    
    public Date readDate(YapReader a_bytes) {
		final long longValue = YLong.readLong(a_bytes);
		if (longValue == Long.MAX_VALUE) {
			return null;
		}
		return new Date(longValue);
	}


}
