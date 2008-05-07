/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.query.processor.*;

/**
 * @exclude
 */
public class QueryingReadContext extends AbstractReadContext {
    
    private final QCandidates _candidates;
    
    private final int _handlerVersion;
    
    private final int _collectionID;
    
    public QueryingReadContext(Transaction transaction, QCandidates candidates, int handlerVersion, ByteArrayBuffer buffer, int collectionID) {
        super(transaction, buffer);
        _candidates = candidates;
        _handlerVersion = handlerVersion;
        _activationDepth = new LegacyActivationDepth(0);
        _collectionID = collectionID;
    }
    
    public QueryingReadContext(Transaction transaction, int handlerVersion, ByteArrayBuffer buffer) {
        this(transaction, null, handlerVersion, buffer, 0);
    }
    
    public int handlerVersion() {
        return _handlerVersion;
    }

    public int collectionID() {
        return _collectionID;
    }
    
    public QCandidates candidates(){
        return _candidates;
    }
    
}
