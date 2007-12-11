/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class StringHandler2 extends StringHandler{

    public StringHandler2(ObjectContainerBase container) {
        super(container);
    }
    
    public Object read(ReadContext context) {
        return readString(context, context);
    }
    
    public void defragment(DefragmentContext context) {
    	context.incrementOffset(linkLength());
    }

}
