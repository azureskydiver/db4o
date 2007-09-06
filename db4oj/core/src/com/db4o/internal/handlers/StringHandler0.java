/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class StringHandler0 extends StringHandler {

    public StringHandler0(StringHandler currentStringHandler) {
        super(currentStringHandler.container(), currentStringHandler.stringIO());
    }
    
    public Object read(ReadContext readContext) {
        UnmarshallingContext context = (UnmarshallingContext) readContext;
        
        Buffer reader =   
            context.container().bufferByAddress(context.readInt(), context.readInt()); 
        
        if (reader == null) {
            return null;
        }
        if (Deploy.debug) {
            reader.readBegin(Const4.YAPSTRING);
        }
        
        String ret = readShort(context.container().configImpl().internStrings(), reader);
        
        if (Deploy.debug) {
            reader.readEnd();
        }
        return ret;
    }

}
