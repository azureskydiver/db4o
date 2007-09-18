/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;


/**
 * @exclude
 */
public class QueryingReadContext extends AbstractReadContext {
    
    private final int _handlerVersion;
    
    public QueryingReadContext(Transaction transaction, int handlerVersion, Buffer buffer) {
        super(transaction, buffer);
        _handlerVersion = handlerVersion;
    }
    
    public int handlerVersion() {
        return _handlerVersion;
    }
    
}
