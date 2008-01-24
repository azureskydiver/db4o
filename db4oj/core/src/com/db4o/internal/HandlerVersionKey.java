/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.internal.fieldhandlers.*;


/**
 * @exclude
 */
public class HandlerVersionKey {
    
    private final FieldHandler _handler;
    
    private final int _version;
    
    public HandlerVersionKey(FieldHandler handler, int version){
        _handler = handler;
        _version = version;
    }

    public int hashCode() {
        return _handler.hashCode() + _version * 4271;
    }

    public boolean equals(Object obj) {
        HandlerVersionKey other = (HandlerVersionKey) obj;
        return _handler.equals(other._handler) && _version == other._version;
    }

}
