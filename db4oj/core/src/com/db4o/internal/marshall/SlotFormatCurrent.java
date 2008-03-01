/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class SlotFormatCurrent extends SlotFormat {

    protected int handlerVersion() {
        return HandlerRegistry.HANDLER_VERSION;
    }

    public int scrollToContent(TypeHandler4 parentHandler, TypeHandler4 arrayElementHandler, ReadBuffer buffer) {
        if(! isIndirectedWithinSlot(parentHandler)){
            return buffer.offset();
        }
        int offset = buffer.offset() + (Const4.INT_LENGTH * 2);
        buffer.seekCurrentInt();
        return offset;
    }
    
    public boolean isIndirectedWithinSlot(TypeHandler4 handler){
        return isVariableLength(handler);
    }

}
