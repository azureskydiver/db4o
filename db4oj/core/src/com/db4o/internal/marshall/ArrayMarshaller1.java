/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;


class ArrayMarshaller1 extends ArrayMarshaller{
    
    public void deleteEmbedded(ArrayHandler arrayHandler, StatefulBuffer buffer) throws Db4oIOException {
        
        int address = buffer.readInt();
        buffer.readInt();  // length
        if (address <= 0) {
            return;
        }
        
        int linkOffSet = buffer._offset; 
        
        Transaction trans = buffer.getTransaction();
        TypeHandler4 typeHandler = arrayHandler._handler;
        
        if (buffer.cascadeDeletes() > 0 && typeHandler instanceof ClassMetadata) {
            buffer._offset = address;
            if (Deploy.debug) {
                buffer.readBegin(arrayHandler.identifier());
            }
            DeleteContext context = new DeleteContext(_family, buffer);
            buffer.setCascadeDeletes(buffer.cascadeDeletes());
            for (int i = arrayHandler.elementCount(trans, buffer); i > 0; i--) {
				arrayHandler._handler.delete(context);
            }
        }
        
        if(linkOffSet > 0){
            buffer._offset = linkOffSet;
        }
    }
    
    protected Buffer prepareIDReader(Transaction trans,Buffer reader) {
        reader._offset = reader.readInt();
        return reader;
    }
    
    public void defragIDs(ArrayHandler arrayHandler,BufferPair readers) {
    	int offset=readers.preparePayloadRead();
        arrayHandler.defrag1(new DefragmentContext(_family, readers, true));
        readers.offset(offset);
    }
}
