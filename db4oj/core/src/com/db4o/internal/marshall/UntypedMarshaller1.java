/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.fieldhandlers.*;

/**
 * @exclude
 */
public class UntypedMarshaller1 extends UntypedMarshaller{
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    
    public TypeHandler4 readArrayHandler(Transaction trans, ByteArrayBuffer[] reader) {
        
        int payLoadOffSet = reader[0].readInt();
        if(payLoadOffSet == 0){
            return null;
        }

        TypeHandler4 ret = null;

        reader[0]._offset = payLoadOffSet;
        
        int yapClassID = reader[0].readInt();
        
        ClassMetadata yc = trans.container().classMetadataForId(yapClassID);
        if(yc != null){
	        TypeHandler4 configuredHandler =
	            trans.container().configImpl().typeHandlerForClass(yc.classReflector(), HandlerRegistry.HANDLER_VERSION);
	        if(configuredHandler != null && configuredHandler instanceof FirstClassHandler){
	            ret = ((FirstClassHandler)configuredHandler).readArrayHandler(trans, _family, reader);
	        }
	        else {
	        	ret = yc.readArrayHandler(trans, _family, reader);
	        }
        }
        return ret;
    }
    
}
