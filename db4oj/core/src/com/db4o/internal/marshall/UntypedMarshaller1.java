/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;

/**
 * @exclude
 */
public class UntypedMarshaller1 extends UntypedMarshaller{
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    
    public TypeHandler4 readArrayHandler(Transaction trans, BufferImpl[] reader) {
        
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
    
	public void defrag(DefragmentContext context) {
        int payLoadOffSet = context.readInt();
        if(payLoadOffSet == 0){
            return;
        }
        int linkOffSet = context.offset();
        context.seek(payLoadOffSet);
        
        int classMetadataID = context.copyIDReturnOriginalID();
        
        ClassMetadata classMetadata = context.classMetadataForId(classMetadataID);
        if(classMetadata != null){
            classMetadata.defragment(context);
        }
        
        context.seek(linkOffSet);
	}
}
