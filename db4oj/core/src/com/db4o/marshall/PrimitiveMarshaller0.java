/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;


public class PrimitiveMarshaller0 {
    
    public void marshall(TypeHandler4 handler, Object obj, YapWriter parentWriter){
        
        int id = 0;
        
        if(obj != null){
        
            Transaction trans = parentWriter.getTransaction();
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
            writer.writeInt(handler.getID());
            handler.writeNew(obj, writer);
            writer.writeEnd();
            trans.i_stream.writeNew(null, writer);
        }
        
        parentWriter.writeInt(id);
    }
    
    protected int objectLength(TypeHandler4 handler, Object obj){
        return handler.linkLength() + YapConst.OBJECT_LENGTH + YapConst.YAPID_LENGTH;
    }
    
    

}
