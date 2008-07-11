/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public abstract class CollectIdContext extends ObjectHeaderContext implements MarshallingInfo, HandlerVersionContext{
    
    private final String _fieldName;
    
    public CollectIdContext(Transaction transaction, ObjectHeader oh, ReadBuffer buffer, String fieldName) {
        super(transaction, buffer, oh);
        _fieldName = fieldName;
    }

    public void addId() {
        int id = readInt();
        if(id <= 0){
            return;
        }
        addIdToTree(id);
    }
    
    public abstract void addIdToTree(int id);

    public ClassMetadata classMetadata() {
        return _objectHeader.classMetadata();
    }

    public String fieldName() {
        return _fieldName;
    }

    public abstract Tree ids();

    public void readID(ReadsObjectIds objectIDHandler) {
        ObjectID objectID = objectIDHandler.readObjectID(this);
        if(objectID.isValid()){
            addIdToTree(objectID._id);
        }
    }


}
