/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class CollectIdContext extends ObjectHeaderContext implements MarshallingInfo{
    
    private final String _fieldName;
    
    private TreeInt _ids;
    
    public CollectIdContext(Transaction transaction, ObjectHeader oh, ReadBuffer buffer, String fieldName) {
        super(transaction, buffer, oh);
        _fieldName = fieldName;
    }

    public String fieldName() {
        return _fieldName;
    }

    public void addId() {
        int id = readInt();
        if(id <= 0){
            return;
        }
        _ids = (TreeInt) Tree.add(_ids, new TreeInt(id));
    }
    
    public ClassMetadata classMetadata() {
        return _objectHeader.classMetadata();
    }

    public Tree ids() {
        return _ids;
    }

}
