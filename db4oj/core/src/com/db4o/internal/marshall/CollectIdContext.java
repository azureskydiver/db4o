/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class CollectIdContext extends ObjectHeaderContext implements MarshallingInfo, HandlerVersionContext{
    
    private final String _fieldName;
    
    private TreeInt _ids;
    
    public CollectIdContext _parentContext;
    
    public CollectIdContext(Transaction transaction, ObjectHeader oh, ReadBuffer buffer, String fieldName) {
        super(transaction, buffer, oh);
        _fieldName = fieldName;
    }

    public CollectIdContext(CollectIdContext context, ObjectHeader header, ReadBuffer buffer) {
        this(context.transaction(), header, buffer, context._fieldName);
        _parentContext = context;
    }

    public String fieldName() {
        return _fieldName;
    }

    public void addId() {
        int id = readInt();
        if(id <= 0){
            return;
        }
        addIdToTree(id);
    }

    private void addIdToTree(int id) {
        if(_parentContext != null){
            _parentContext.addIdToTree(id);
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

    public void readID(ReadsObjectIds objectIDHandler) {
        ObjectID objectID = objectIDHandler.readObjectID(this);
        if(objectID.isValid()){
            addIdToTree(objectID._id);
        }
    }

}
