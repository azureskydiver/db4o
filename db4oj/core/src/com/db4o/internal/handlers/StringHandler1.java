/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.foundation.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class StringHandler1 extends StringHandler {

    public StringHandler1(StringHandler currentStringHandler) {
        super(currentStringHandler.container(), currentStringHandler.stringIO());
    }
    
    public Object read(ReadContext context) {
        // TODO Auto-generated method stub
        throw new NotImplementedException();
    }

}
