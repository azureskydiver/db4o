/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class UntypedFieldHandler2 extends UntypedFieldHandler {
    
    public UntypedFieldHandler2(ObjectContainerBase container) {
        super(container);
    }
    
    protected void seekSecondaryOffset(ReadBuffer buffer, TypeHandler4 typeHandler) {
        if(isPrimitiveArray(typeHandler)){
            buffer.seekCurrentInt();
        }
    }


}
