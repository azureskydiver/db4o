/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;


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

    public int scrollToContentReturnLinkOffset(HandlerRegistry handlerRegistry, TypeHandler4 parentHandler, TypeHandler4 arrayElementHandler, ReadBuffer buffer) {
        if(arrayElementHandler instanceof ArrayHandler){
            int offset = buffer.offset() + Const4.INT_LENGTH;
            buffer.seekCurrentInt();
            return offset;
        }
        return buffer.offset();
    }

}
