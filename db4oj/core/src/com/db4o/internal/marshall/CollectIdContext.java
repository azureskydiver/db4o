/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class CollectIdContext extends AbstractReadContext implements MarshallingInfo{
    
    private final String _fieldName;
    
    private TreeInt _ids;
    
    private final ObjectHeader _objectHeader;

    public CollectIdContext(Transaction transaction, ObjectHeader oh, ReadBuffer buffer, String fieldName) {
        super(transaction, buffer);
        _fieldName = fieldName;
        _objectHeader = oh;
    }

    public String fieldName() {
        return _fieldName;
    }

    public void addId() {
        _ids = (TreeInt) Tree.add(_ids, new TreeInt(readInt()));
    }
    
    
    // The following three methods are similar to 
    // the ones in UnmarshallingContext

    public ClassMetadata classMetadata() {
        return _objectHeader.classMetadata();
    }

    public int handlerVersion() {
        return _objectHeader.handlerVersion();
    }
    
    public ObjectHeaderAttributes headerAttributes(){
        return _objectHeader._headerAttributes;
    }

    public boolean isNull(int fieldIndex) {
        return headerAttributes().isNull(fieldIndex);
    }

    public Tree ids() {
        return _ids;
    }

    

}
