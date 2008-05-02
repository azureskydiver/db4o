/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;



/**
 * @exclude
 */
public class ArrayHandler2 extends ArrayHandler4 {
    
    protected int preparePayloadRead(DefragmentContext context) {
		return context.offset();
    }

}
