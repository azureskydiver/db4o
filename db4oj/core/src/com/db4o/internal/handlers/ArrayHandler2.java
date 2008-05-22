/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.handlers;

import com.db4o.internal.*;



/**
 * @exclude
 */
public class ArrayHandler2 extends ArrayHandler3 {
    
    /**
     * FIXME: We are not changing any behaviour, why do we override?
     */
    protected int preparePayloadRead(DefragmentContext context) {
		return context.offset();
    }

}
