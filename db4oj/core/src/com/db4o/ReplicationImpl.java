/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;

/**
 * 
 */
class ReplicationImpl implements ReplicationProcess {

    final YapStream   _peerA;
    final Transaction _transA;

    final YapStream         _peerB;
    final Transaction       _transB;

    final ReplicationConflictHandler      _conflictHandler;

    final ReplicationRecord i_record;

    final Db4oDatabase      _databaseAinB;

    private Object    _objectA;
    private Object    _objectB;
    
    private YapObject _yapObjectA;
    
    private int i_direction; 
    
    private static final int IGNORE = 0;
    private static final int TO_B = -1;
    private static final int TO_A = 1;


    ReplicationImpl(YapStream peerA, ObjectContainer peerB, ReplicationConflictHandler conflictHandler) {
        _peerA = peerA;
        _transA = peerA.checkTransaction(null);
        
        _peerB = (YapStream) peerB;
        _transB = _peerB.checkTransaction(null);

        _peerA.i_handlers.i_replication = this;
        _peerA.i_migrateFrom = _peerB;

        _peerB.i_handlers.i_replication = this;
        _peerB.i_migrateFrom = _peerA;
        
        _conflictHandler = conflictHandler;

        _databaseAinB = _peerB.i_handlers
            .ensureDb4oDatabase(_transB, _peerA.identity());
        ObjectSet objectSet = queryForReplicationRecord();
        if (objectSet.hasNext()) {
            i_record = (ReplicationRecord) objectSet.next();
        } else {
            i_record = new ReplicationRecord();
            i_record.i_source = _databaseAinB;
            i_record.i_target = _peerB.identity();
        }
    }

    public void commit() {
        _peerA.commit();
        _peerB.commit();

        long i_sourceVersion = _peerA.currentVersion() - 1;
        long i_destinationVersion = _peerB.currentVersion() - 1;

        i_record.i_version = i_destinationVersion;

        if (i_sourceVersion > i_destinationVersion) {
            i_record.i_version = i_sourceVersion;
            _peerB.raiseVersion(i_record.i_version);
        } else if (i_destinationVersion > i_sourceVersion) {
            _peerA.raiseVersion(i_record.i_version);
            _peerA.commit();
        }
        _peerB.showInternalClasses(true);
        _peerB.set(i_record);
        _peerB.commit();
        _peerB.showInternalClasses(false);

        endReplication();
    }

    public void rollback() {
        if (_peerB != null) {
            _peerB.rollback();
        }
        _peerA.rollback();
        endReplication();
    }

    private void endReplication() {
        _peerA.i_migrateFrom = null;
        _peerA.i_handlers.i_replication = null;
        _peerB.i_migrateFrom = null;
        _peerB.i_handlers.i_replication = null;
    }

    private ObjectSet queryForReplicationRecord() {
        _peerB.showInternalClasses(true);
        Query q = _peerB.querySharpenBug();
        q.constrain(YapConst.CLASS_REPLICATIONRECORD);
        q.descend("i_source").constrain(_databaseAinB).identity();
        q.descend("i_target").constrain(_peerB.identity()).identity();
        ObjectSet objectSet = q.execute();
        _peerB.showInternalClasses(false);
        return objectSet;
    }

    boolean toDestination(Object a_sourceObject) {
        synchronized(_peerA.i_lock){
            _objectA = a_sourceObject;
	        _yapObjectA = _peerA.getYapObject(a_sourceObject);
	        if (_yapObjectA != null) {
	            VirtualAttributes vas = _yapObjectA.virtualAttributes(_transA);
	            if (vas != null) {
	                
	                Object[] arr = _transB.objectAndYapObjectBySignature(vas.i_uuid, vas.i_database.i_signature); 
	                if (arr[0] != null) {
	                    YapObject yob = (YapObject) arr[1];
	                    _objectB = arr[0];
	                    VirtualAttributes vad = yob
	                        .virtualAttributes(_transB);
	                    if (vas.i_version <= i_record.i_version
	                        && vad.i_version <= i_record.i_version) {
	                        _peerB.bind2(yob, _objectA);
	                        return true;
	                    }
	                    
	                    if (vas.i_version > i_record.i_version
	                        && vad.i_version > i_record.i_version) {
	                        
	                        i_direction = IGNORE;
	                        
	                        Object prevailing = _conflictHandler.resolveConflict(this, _objectA, _objectB);
	                        
	                        if(prevailing == _objectA){
	                        	i_direction = TO_B; 
	                        }
	                        
	                        if(prevailing == _objectB){
	                        	i_direction = TO_A; 
	                        }
	                        
	                        
	                        if(i_direction == IGNORE){
	                            _peerB.bind2(yob, _objectA);
	                            return true;
	                        }
	                        
	                    }else{
	                        i_direction = TO_B;
	                        if(vad.i_version > i_record.i_version){
	                            i_direction = TO_A;
	                        }
	                    }
	                    
	                    if(i_direction == TO_A){
	                        if(! yob.isActive()){
	                            yob.activate(_transB, _objectB, 1, false);
	                        }
	                        _peerA.bind2(_yapObjectA, _objectB);
	                        _peerA.setNoReplication(_transA, _objectB, 1, true);
	                    }else{
	                        if( ! _yapObjectA.isActive()){
	                            _yapObjectA.activate(_transA, _objectA, 1, false);
	                        }
		                    _peerB.bind2(yob, _objectA);
		                    _peerB.setNoReplication(_transB, _objectA, 1, true);
	                    }
	                    
	                    return true;
	                }
	            }
	        }
	        return false;
        }
    }
    
    void destinationOnNew(YapObject a_yod){
        if(_yapObjectA != null){
            VirtualAttributes vas = _yapObjectA.virtualAttributes(_transA);
            a_yod.i_virtualAttributes = new VirtualAttributes();
            VirtualAttributes vad = a_yod.i_virtualAttributes;
            vad.i_uuid = vas.i_uuid;
            vad.i_version = vas.i_version;
            vad.i_database = vas.i_database;
        }
    }
    
	private long lastSynchronization() {
		return i_record.i_version;
	}

	public void replicate(Object obj) {
		
	   // FIXME: _peerB.set(obj);
	}

	/* (non-Javadoc)
	 * @see com.db4o.replication.ReplicationProcess#setDirection(com.db4o.ObjectContainer, com.db4o.ObjectContainer)
	 */
	public void setDirection(ObjectContainer relicateFrom, ObjectContainer replicateTo) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.db4o.replication.ReplicationProcess#checkConflict(java.lang.Object)
	 */
	public void checkConflict(Object obj) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.db4o.replication.ReplicationProcess#constrainModified(com.db4o.query.Query)
	 */
	public void whereModified(Query query) {
		query.descend(VirtualField.VERSION).constrain(lastSynchronization()).greater();
	}

	/* (non-Javadoc)
	 * @see com.db4o.replication.ReplicationProcess#force(java.lang.Object)
	 */
	public void force(Object obj) throws RuntimeException {
		// TODO Auto-generated method stub
		
	}

	public ObjectContainer peerA() {
		return _peerA;
	}

	public ObjectContainer peerB() {
		return _peerB;
	}

    

}