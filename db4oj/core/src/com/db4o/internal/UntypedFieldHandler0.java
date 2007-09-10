/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.marshall.*;


/**
 * @exclude
 */
public class UntypedFieldHandler0 extends UntypedFieldHandler {

    public UntypedFieldHandler0(ObjectContainerBase container) {
        super(container);
    }
    
    public Object read(ReadContext context) {
        return context.readObject();
    }

}
