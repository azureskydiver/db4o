/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.VirtualField;
import com.db4o.query.Query;
import com.db4o.replication.ReplicationConflictHandler;
import com.db4o.replication.ReplicationProcess;

/**
 * 
 */
class ReplicationImpl implements ReplicationProcess {

    final YapStream   _peerA;
    final Transaction _transA;

    final YapStream         _peerB;
    final Transaction       _transB;

    final ReplicationConflictHandler      _conflictHandler;

    final ReplicationRecord _record;

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

        _record = ReplicationRecord.beginReplication(_transA, _transB);
    }

    public void commit() {
        _peerA.commit();
        _peerB.commit();

        long versionA = _peerA.currentVersion() - 1;
        long versionB = _peerB.currentVersion() - 1;

        _record._version = versionB;

        if (versionA > versionB) {
            _record._version = versionA;
            _peerB.raiseVersion(_record._version);
        } else if (versionB > versionA) {
            _peerA.raiseVersion(_record._version);
        }
        
        _record.store(_peerA);
        _record.store(_peerB);

        endReplication();
    }

    public void rollback() {
        _peerA.rollback();
        _peerB.rollback();
        endReplication();
    }

    private void endReplication() {
        _peerA.i_migrateFrom = null;
        _peerA.i_handlers.i_replication = null;
        _peerB.i_migrateFrom = null;
        _peerB.i_handlers.i_replication = null;
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
	                    if (vas.i_version <= _record._version
	                        && vad.i_version <= _record._version) {
	                        _peerB.bind2(yob, _objectA);
	                        return true;
	                    }
	                    
	                    if (vas.i_version > _record._version
	                        && vad.i_version > _record._version) {
	                        
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
	                        if(vad.i_version > _record._version){
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
		return _record._version;
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
		query.descend(VirtualField.VERSION).constrain(new Long(lastSynchronization())).greater();
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