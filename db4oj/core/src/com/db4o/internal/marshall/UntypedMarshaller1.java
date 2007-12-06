/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.ext.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class UntypedMarshaller1 extends UntypedMarshaller{
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    public void deleteEmbedded(StatefulBuffer reader) throws Db4oIOException {
        int payLoadOffset = reader.readInt();
        if (payLoadOffset > 0) {
            int linkOffset = reader._offset;
            reader._offset = payLoadOffset;
            int yapClassID = reader.readInt();
            ClassMetadata yc = reader.getStream().classMetadataForId(yapClassID);
            if(yc != null){
                yc.deleteEmbedded(_family, reader);
            }
            reader._offset = linkOffset;
        }
    }
    
    public TypeHandler4 readArrayHandler(Transaction trans, Buffer[] reader) {
        
        int payLoadOffSet = reader[0].readInt();
        if(payLoadOffSet == 0){
            return null;
        }

        TypeHandler4 ret = null;

        reader[0]._offset = payLoadOffSet;
        
        int yapClassID = reader[0].readInt();
        
        ClassMetadata yc = trans.container().classMetadataForId(yapClassID);
        if(yc != null){
            ret = yc.readArrayHandler(trans, _family, reader);
        }
        return ret;
    }
    
	public void defrag(BufferPair readers) {
        int payLoadOffSet = readers.readInt();
        if(payLoadOffSet == 0){
            return;
        }
        int linkOffSet = readers.offset();
        readers.offset(payLoadOffSet);
        
        int yapClassID = readers.copyIDAndRetrieveMapping().orig();
        
        ClassMetadata yc = readers.context().yapClass(yapClassID);
        if(yc != null){
            yc.defragment(new DefragmentContext(_family, readers, false));
        }
        
        readers.offset(linkOffSet);
	}
}
