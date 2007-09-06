/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class StringHandler2 extends StringHandler{

    public StringHandler2(ObjectContainerBase container, LatinStringIO stringIO) {
        super(container, stringIO);
    }
    
    public Object read(ReadContext context) {
        
        if (Deploy.debug) {
            Debug.readBegin(context, Const4.YAPSTRING);
        }
        
        int length = context.readInt();
        
        Object result = stringIo(context).read(context, length);
        
        if (Deploy.debug) {
            Debug.readEnd(context);
        }
        
        return result;
    }


}
