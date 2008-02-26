/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.marshall.*;


/**
 * @exclude
 */
public class IntHandler0 extends IntHandler {

    public Object read(ReadContext context) {
        int i = context.readInt();
        if (i == Integer.MAX_VALUE) {
            return null;
        }
        return new Integer(i);
    }
    
}
