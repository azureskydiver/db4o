/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class SlotFormat0 extends SlotFormat {

    protected int handlerVersion() {
        return 0;
    }

    public void scrollToContent(HandlerRegistry handlerRegistry, TypeHandler4 parentHandler, TypeHandler4 arrayElementHandler, ReadBuffer buffer) {
        if(arrayElementHandler instanceof ArrayHandler){
            buffer.seekCurrentInt();
        }
    }

}
