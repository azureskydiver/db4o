/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.query.processor.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class QueryingReadContext extends AbstractReadContext implements HandlerVersionContext{
    
    private final QCandidates _candidates;
    
    private final int _collectionID;
    
    private final int _handlerVersion;
    
    private TreeInt _ids;
    
    private List4 _objectsWithoutId;

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
    
    private void addId(int id) {
        _ids = (TreeInt) Tree.add(_ids, new TreeInt(id));
    }
    
    public Tree ids() {
        return _ids;
    }
    
    public void add(Object obj) {
        int id = container().getID(transaction(), obj);
        if(id > 0){
            addId(id);
            return;
        }
        addObjectWithoutId(obj);
    }
    
    public void readId(TypeHandler4 handler) {
        ObjectID objectID = ObjectID.NOT_POSSIBLE;
        try {
            int offset = offset();
            if(handler instanceof ReadsObjectIds){
                objectID = ((ReadsObjectIds)handler).readObjectID(this);
            }
            if(objectID.isValid()){
                addId(objectID._id);
                return;
            }
            if(objectID == ObjectID.NOT_POSSIBLE){
                seek(offset);
                Object obj = read(handler);
                if(obj != null){
                    addObjectWithoutId(obj);
                }
            }
            
        } catch (Exception e) {
            // FIXME: Catchall
        }
    }

    private void addObjectWithoutId(Object obj) {
        _objectsWithoutId = new List4(_objectsWithoutId, obj);
    }

    public void skipId(TypeHandler4 handler) {
        if(handler instanceof ReadsObjectIds){
            ((ReadsObjectIds)handler).readObjectID(this);
            return;
        }
        // TODO: Optimize for just doing a seek here.
        read(handler);
    }
    
    public Iterator4 objectsWithoutId(){
        return new Iterator4Impl(_objectsWithoutId);
    }
    
}
