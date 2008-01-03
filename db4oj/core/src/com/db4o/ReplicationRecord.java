/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.*;

/**
 * tracks the version of the last replication between
 * two Objectcontainers.
 * 
 * @exclude
 * @persistent
 */
public class ReplicationRecord implements Internal4{
   
    public Db4oDatabase _youngerPeer;
    public Db4oDatabase _olderPeer;
    public long _version;
    
    public ReplicationRecord(){
    }
    
    public ReplicationRecord(Db4oDatabase younger, Db4oDatabase older){
        _youngerPeer = younger;
        _olderPeer = older;
    }
    
    public void setVersion(long version){
        _version = version;
    }
    
    public void store(ObjectContainerBase container){
        container.showInternalClasses(true);
        try {
	        Transaction trans = container.checkTransaction();
	        container.storeAfterReplication(trans, this, 1, false);
	        container.commit(trans);
        } finally {
        	container.showInternalClasses(false);
        }
    }
    
    public static ReplicationRecord beginReplication(Transaction transA, Transaction  transB){
        
        ObjectContainerBase peerA = transA.container();
        ObjectContainerBase peerB = transB.container();
        
        Db4oDatabase dbA = ((InternalObjectContainer)peerA).identity();
        Db4oDatabase dbB = ((InternalObjectContainer)peerB).identity();
        
        dbB.bind(transA);
        dbA.bind(transB);
        
        Db4oDatabase younger = null;
        Db4oDatabase older = null;
        
        if(dbA.isOlderThan(dbB)){
            younger = dbB;
            older = dbA;
        }else{
            younger = dbA;
            older = dbB;
        }
        
        ReplicationRecord rrA = queryForReplicationRecord(peerA, transA, younger, older);
        ReplicationRecord rrB = queryForReplicationRecord(peerB, transB, younger, older);
        if(rrA == null){
            if(rrB == null){
                return new ReplicationRecord(younger, older);
            }
            rrB.store(peerA);
            return rrB;
        }
        
        if(rrB == null){
            rrA.store(peerB);
            return rrA;
        }
        
        if(rrA != rrB){
            peerB.showInternalClasses(true);
            try {
	            int id = peerB.getID(transB, rrB);
	            peerB.bind(transB, rrA, id);
	        } finally {
            	peerB.showInternalClasses(false);
            }
        }
        
        return rrA;
    }
    
    public static ReplicationRecord queryForReplicationRecord(ObjectContainerBase container, Transaction trans, Db4oDatabase younger, Db4oDatabase older) {
        container.showInternalClasses(true);
        try {
	        Query q = container.query(trans);
	        q.constrain(Const4.CLASS_REPLICATIONRECORD);
	        q.descend("_youngerPeer").constrain(younger).identity();
	        q.descend("_olderPeer").constrain(older).identity();
	        ObjectSet objectSet = q.execute();
	        return objectSet.hasNext() 
	        	? (ReplicationRecord)objectSet.next()
	        	: null;
        } finally {
        	container.showInternalClasses(false);
        }
    }
}

