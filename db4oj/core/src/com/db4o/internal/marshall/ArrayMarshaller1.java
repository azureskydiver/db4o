/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;


class ArrayMarshaller1 extends ArrayMarshaller{
    
    public void deleteEmbedded(ArrayHandler arrayHandler, StatefulBuffer reader) throws Db4oIOException {
        
        int address = reader.readInt();
        reader.readInt();  // length
        if (address <= 0) {
            return;
        }
        
        int linkOffSet = reader._offset; 
        
        Transaction trans = reader.getTransaction();
        TypeHandler4 typeHandler = arrayHandler._handler;
        
        if (reader.cascadeDeletes() > 0 && typeHandler instanceof ClassMetadata) {
            reader._offset = address;
            if (Deploy.debug) {
                reader.readBegin(arrayHandler.identifier());
            }
            reader.setCascadeDeletes(reader.cascadeDeletes());
            for (int i = arrayHandler.elementCount(trans, reader); i > 0; i--) {
                arrayHandler._handler.deleteEmbedded(_family, reader);
            }
        }
        
        if(linkOffSet > 0){
            reader._offset = linkOffSet;
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
