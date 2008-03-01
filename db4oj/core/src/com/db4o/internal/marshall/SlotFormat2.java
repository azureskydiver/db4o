/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class SlotFormat2 extends SlotFormat {

    protected int handlerVersion() {
        return 2;
    }

    public boolean isIndirectedWithinSlot(TypeHandler4 handler) {
        return isVariableLength(handler);
    }

}
