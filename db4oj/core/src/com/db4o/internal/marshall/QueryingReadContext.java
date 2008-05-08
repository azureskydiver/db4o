/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class QueryingReadContext extends AbstractReadContext {
    
    private final QCandidates _candidates;
    
    private final int _collectionID;
    
    private final int _handlerVersion;

    public QueryingReadContext(Transaction transaction, QCandidates candidates, int handlerVersion, ReadBuffer buffer, int collectionID) {
        super(transaction, buffer);
        _candidates = candidates;
        _activationDepth = new LegacyActivationDepth(0);
        _collectionID = collectionID;
        _handlerVersion = handlerVersion;
    }
    
    public QueryingReadContext(Transaction transaction, int handlerVersion, ReadBuffer buffer) {
        this(transaction, null, handlerVersion, buffer, 0);
    }
    
    public int collectionID() {
        return _collectionID;
    }
    
    public QCandidates candidates(){
        return _candidates;
    }
    
    public int handlerVersion() {
        return _handlerVersion;
    }
    

    
}
