/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class CollectIdContext extends ObjectHeaderContext implements MarshallingInfo, HandlerVersionContext{
    
    private final IdObjectCollector _collector = new IdObjectCollector();
    
    public CollectIdContext(Transaction transaction, ObjectHeader oh, ReadBuffer buffer) {
        super(transaction, buffer, oh);
    }
    

    public void addId() {
        int id = readInt();
        if(id <= 0){
            return;
        }
        addId(id);
    }

    private void addId(int id) {
        _collector.addId(id);
    }
    
    public ClassMetadata classMetadata() {
        return _objectHeader.classMetadata();
    }

    public Tree ids(){
        return _collector.ids();
    }

    public void readID(ReadsObjectIds objectIDHandler) {
        ObjectID objectID = objectIDHandler.readObjectID(this);
        if(objectID.isValid()){
            addId(objectID._id);
        }
    }
    
    public IdObjectCollector collector(){
        return _collector;
    }

}
