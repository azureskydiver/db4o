/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

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

    private YapObject _yapObjectA;
    
    private int _direction;
    
    private static final int IGNORE = 0;
    private static final int TO_B = -1;
    private static final int TO_A = 1;
    private static final int CHECK_CONFLICT = -99;


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


    boolean process(Object objectA) {
        synchronized(_peerA.i_lock){
	        _yapObjectA = _peerA.getYapObject(objectA);
	        if (_yapObjectA != null) {
	            VirtualAttributes attA = _yapObjectA.virtualAttributes(_transA);
	            if (attA != null) {
	                Object[] arr = _transB.objectAndYapObjectBySignature(attA.i_uuid, attA.i_database.i_signature); 
	                if (arr[0] != null) {
	                    YapObject yapObjectB = (YapObject) arr[1];
	                    Object objectB = arr[0];
	                    VirtualAttributes attB = yapObjectB.virtualAttributes(_transB);
	                    if (attA.i_version <= _record._version
	                        && attB.i_version <= _record._version) {
                            
                            if(_direction != CHECK_CONFLICT){
                                _peerB.bind2(yapObjectB, objectA);
                            }
                            
	                        return true;
	                    }
                        
                        int direction = ignore();
	                    
	                    if (attA.i_version > _record._version
	                        && attB.i_version > _record._version) {
	                        
	                        Object prevailing = _conflictHandler.resolveConflict(this, objectA, objectB);
	                        
	                        if(prevailing == objectA){
                                direction = toB();
	                        }
	                        
	                        if(prevailing == objectB){
                                direction = toA();
	                        }
	                        
	                        if(direction == IGNORE){
	                            _peerB.bind2(yapObjectB, objectA);
	                            return true;
	                        }
	                        
	                    }else{
	                        direction = toB();
	                        if(attB.i_version > _record._version){
                                direction = toA();
	                        }
	                    }
	                    
	                    if(direction == TO_A){
	                        if(! yapObjectB.isActive()){
	                            yapObjectB.activate(_transB, objectB, 1, false);
	                        }
	                        _peerA.bind2(_yapObjectA, objectB);
	                        _peerA.setNoReplication(_transA, objectB, 1, true);
	                    }
                        
                        if(direction == TO_B){
	                        if( ! _yapObjectA.isActive()){
	                            _yapObjectA.activate(_transA, objectA, 1, false);
	                        }
		                    _peerB.bind2(yapObjectB, objectA);
		                    _peerB.setNoReplication(_transB, objectA, 1, true);
	                    }
	                    
	                    return true;
	                }
	            }
	        }
	        return false;
        }
    }
    
    private int toA(){
        if(_direction == CHECK_CONFLICT){
            return CHECK_CONFLICT;
        }
        if(_direction != TO_B){
            return TO_A;
        }
        return IGNORE;
    }
    
    private int toB(){
        if(_direction == CHECK_CONFLICT){
            return CHECK_CONFLICT;
        }
        if(_direction != TO_A){
            return TO_B;
        }
        return IGNORE;
    }
    
    private int ignore(){
        if(_direction == CHECK_CONFLICT){
            return CHECK_CONFLICT;
        }
        return IGNORE;
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

        // When there is an active replication process, the set() method
        // will call back to the #process() method in this class.
        
        // This detour is necessary, since #set() has to handle all cases
        // anyway, for members of the replicated object, especially the
        // prevention of endless loops in case of circular references.
        
        
        YapStream stream = _peerB;
        
        if(_peerB.isStored(obj)){
            if(! _peerA.isStored(obj)){
                stream = _peerA;
            }
        }
        
	   stream.set(obj);  
	}

	public void setDirection(ObjectContainer replicateFrom, ObjectContainer replicateTo) {
        if(replicateFrom == _peerA && replicateTo == _peerB){
            _direction = TO_B;
        }
        if(replicateFrom == _peerB && replicateTo == _peerA){
            _direction = TO_A;
        }
	}

	public void checkConflict(Object obj) {
		int temp = _direction;
        _direction = CHECK_CONFLICT;
        replicate(obj);
        _direction = temp;
	}

	public void whereModified(Query query) {
		query.descend(VirtualField.VERSION).constrain(new Long(lastSynchronization())).greater();
	}

	public ObjectContainer peerA() {
		return _peerA;
	}

	public ObjectContainer peerB() {
		return _peerB;
	}
    

}