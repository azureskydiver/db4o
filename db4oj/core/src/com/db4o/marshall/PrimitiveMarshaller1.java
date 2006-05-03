/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


public class PrimitiveMarshaller1 extends PrimitiveMarshaller {
    
    public int marshall(Transaction trans, YapClassPrimitive yapClassPrimitive, Object obj, YapWriter parentWriter){
        
        int id = 0;
        
        if(obj != null){
            
            TypeHandler4 handler = yapClassPrimitive.i_handler;
        
            YapStream stream = trans.i_stream;
            id = stream.newUserObject();
            int address = -1;
            int length = objectLength(handler, obj);
            if(! trans.i_stream.isClient()){
                address = trans.i_file.getSlot(length); 
            }
            trans.setPointer(id, address, length);
            
            YapWriter writer = new YapWriter(trans, length);
            writer.useSlot(id, address, length);
            if (Deploy.debug) {
                writer.writeBegin(YapConst.YAPOBJECT, length);
            }
            writer.writeInt(yapClassPrimitive.getID());
            
            // FIXME: SM Temporary fix to marshall strings in untyped variables to             // old format
            handler.writeNew(MarshallerFamily.forVersion(0), obj, writer);
            
            writer.writeEnd();
            trans.i_stream.writeNew(yapClassPrimitive, writer);
        }
        
        if(parentWriter != null){
            parentWriter.writeInt(id);
        }
        
        return id;
    }


}
