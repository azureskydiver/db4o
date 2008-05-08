/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public abstract class ObjectHeaderContext extends AbstractReadContext {
    
    private final ObjectHeader _objectHeader;
    
    protected ObjectHeaderContext(Transaction transaction, ReadBuffer buffer, ObjectHeader objectHeader) {
        super(transaction, buffer);
        _objectHeader = objectHeader;
    }
    
    public ObjectHeaderAttributes headerAttributes(){
        return _objectHeader._headerAttributes;
    }

    public boolean isNull(int fieldIndex) {
        return headerAttributes().isNull(fieldIndex);
    }

    public int handlerVersion() {
        return _objectHeader.handlerVersion();
    }


    


}
