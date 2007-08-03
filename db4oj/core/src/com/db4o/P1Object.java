/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.replication.*;

/**
 * base class for all database aware objects
 * @exclude
 * @persistent
 */
public class P1Object implements Db4oTypeImpl{
    
    private transient Transaction i_trans;
    private transient ObjectReference i_yapObject;
    
    public P1Object(){
    }
    
    P1Object(Transaction a_trans){
        i_trans = a_trans;
    }
    
    public void activate (Object a_obj, int a_depth){
        if(i_trans == null){
        	return;
        }
        if(a_depth < 0){
            stream().activateDefaultDepth(i_trans, a_obj);
        }else{
            stream().activate(i_trans, a_obj, a_depth);
        }
    }
    
    public int activationDepth(){
        return 1;
    }
    
    public int adjustReadDepth(int a_depth) {
        return a_depth;
    }
    
    public boolean canBind() {
        return false;
    }
    
    public void checkActive(){
        if(i_trans == null){
        	return;
        }
	    if(i_yapObject == null){
	        
	        i_yapObject = i_trans.referenceForObject(this);
	        if(i_yapObject == null){
	            stream().set(this);
	            i_yapObject = i_trans.referenceForObject(this);
	        }
	    }
	    if(validYapObject()){
	    	i_yapObject.activate(i_trans, this, activationDepth(), false);
	    }
    }

    public Object createDefault(Transaction a_trans) {
        throw Exceptions4.virtualException();
    }
    
    void deactivate(){
        if(validYapObject()){
            i_yapObject.deactivate(i_trans, activationDepth());
        }
    }
    
    void delete(){
        if(i_trans == null){
        	return;
        }
        if(i_yapObject == null){
            i_yapObject = i_trans.referenceForObject(this);
        }
        if(validYapObject()){
            stream().delete2(i_trans,i_yapObject,this, 0, false);
        }
    }
    
    protected void delete(Object a_obj){
        if(i_trans != null){
            stream().delete(a_obj);
        }
    }
    
    protected long getIDOf(Object a_obj){
        if(i_trans == null){
            return 0;
        }
        return stream().getID(a_obj);
    }
    
    protected Transaction getTrans(){
        return i_trans;
    }
    
    public boolean hasClassIndex() {
        return false;
    }
    
    public void preDeactivate(){
        // virtual, do nothing
    }
	
    /**
	 * @deprecated
	 */
    protected Object replicate(Transaction fromTrans, Transaction toTrans) {
        
        ObjectContainerBase fromStream = fromTrans.container();
        ObjectContainerBase toStream = toTrans.container();
        
        MigrationConnection mgc = fromStream._handlers.migrationConnection();
        
        synchronized(fromStream.lock()){
            
    		int id = toStream.oldReplicationHandles(toTrans, this);
            
            if(id == -1){
                // no action to be taken, already handled
                return this;
            }
            
    		if(id > 0) {
                // replication has taken care, we need that object
    			return toStream.getByID(id);
    		}
            
            if(mgc != null){
                Object otherObj = mgc.identityFor(this);
                if(otherObj != null){
                    return otherObj;
                }
            }
            
            P1Object replica = (P1Object)createDefault(toTrans);
            
            if(mgc != null){
                mgc.mapReference(replica, i_yapObject);
                mgc.mapIdentity(this, replica);
            }
			
            replica.store(0);
			
            return replica;
        }
	}
    
    public void replicateFrom(Object obj) {
        // do nothing
    }

    public void setTrans(Transaction a_trans){
        i_trans = a_trans;
    }

    public void setObjectReference(ObjectReference a_yapObject) {
        i_yapObject = a_yapObject;
    }
    
    protected void store(Object a_obj){
        if(i_trans != null){
            stream().setInternal(i_trans, a_obj, true);
        }
    }
    
    public Object storedTo(Transaction a_trans){
        i_trans = a_trans;
        return this;
    }
    
    Object streamLock(){
        if(i_trans != null){
	        stream().checkClosed();
	        return stream().lock();
        }
        return this;
    }
    
    public void store(int a_depth){
        if(i_trans == null){
        	return;
        }
        if(i_yapObject == null){
            i_yapObject = i_trans.referenceForObject(this);
            if(i_yapObject == null){
                i_trans.container().setInternal(i_trans, this, true);
                i_yapObject = i_trans.referenceForObject(this);
                return;
            }
        }
        update(a_depth);
    }
    
    void update(){
        update(activationDepth());
    }
    
    void update(int depth){
        if(validYapObject()){
            ObjectContainerBase stream = stream();
            stream.beginTopLevelSet();
            try{
	            i_yapObject.writeUpdate(i_trans, depth);
	            stream.checkStillToSet();
	            stream.completeTopLevelSet();
            } catch(Db4oException e) {
            	stream.completeTopLevelSet(e);
            } finally{
            	stream.endTopLevelSet(i_trans);
            }
        }
    }
    
    void updateInternal(){
        updateInternal(activationDepth());
    }
    
    void updateInternal(int depth){
        if(validYapObject()){
            i_yapObject.writeUpdate(i_trans, depth);
            stream().flagAsHandled(i_yapObject);
            stream().checkStillToSet();
        }
    }
    
    private boolean validYapObject(){
        return (i_trans != null) && (i_yapObject != null) && (i_yapObject.getID() > 0);
    }
    
    private ObjectContainerBase stream(){
    	return i_trans.container();
    }
    
}
