/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;

/**
 * @exclude
 */
public class QueryingReadContext extends AbstractReadContext {
    
    private final int _handlerVersion;
    
    public QueryingReadContext(Transaction transaction, int handlerVersion, ByteArrayBuffer buffer) {
        super(transaction, buffer);
        _handlerVersion = handlerVersion;
        _activationDepth = new LegacyActivationDepth(0);
    }
    
    public int handlerVersion() {
        return _handlerVersion;
    }
    
}
