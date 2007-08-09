/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.replication.*;
import com.db4o.query.*;
import com.db4o.replication.*;

/**
 * @exclude
 * @deprecated
 */
public class ReplicationImpl implements ReplicationProcess {

    final ObjectContainerBase _peerA;

    final Transaction _transA;

    final ObjectContainerBase _peerB;

    final Transaction _transB;

    final ReplicationConflictHandler _conflictHandler;

    final ReplicationRecord _record;

    private int _direction;

    private static final int IGNORE = 0;

    private static final int TO_B = -1;

    private static final int TO_A = 1;

    private static final int CHECK_CONFLICT = -99;
    
	public ReplicationImpl(ObjectContainerBase peerA, ObjectContainerBase peerB,
			ReplicationConflictHandler conflictHandler) {
        
        if(conflictHandler == null){
            // We don't allow starting replication without a 
            // conflict handler, so we don't get late failures.
            throw new NullPointerException();
        }
        
        synchronized (peerA.lock()) {
			synchronized (peerB.lock()) {

				_peerA = peerA;
				_transA = peerA.checkTransaction();

				_peerB = peerB;
				_transB = _peerB.checkTransaction(null);

				MigrationConnection mgc = new MigrationConnection(_peerA, _peerB);

				_peerA._handlers.migrationConnection(mgc);
				_peerA._handlers.replication(this);
				_peerA.replicationCallState(Const4.OLD);

				_peerB._handlers.migrationConnection(mgc);
				_peerB._handlers.replication(this);
                _peerB.replicationCallState(Const4.OLD);

				_conflictHandler = conflictHandler;

				_record = ReplicationRecord.beginReplication(_transA, _transB);
			}
		}
        
	}

    private int bindAndSet(Transaction trans, ObjectContainerBase peer, ObjectReference ref, Object sourceObject){
        if(sourceObject instanceof Db4oTypeImpl){
            Db4oTypeImpl db4oType = (Db4oTypeImpl)sourceObject;
            if(! db4oType.canBind()){
                Db4oTypeImpl targetObject = (Db4oTypeImpl)ref.getObject();
                targetObject.replicateFrom(sourceObject);
                return ref.getID();
            }
        }
        peer.bind2(trans, ref, sourceObject);
        return peer.setAfterReplication(trans, sourceObject, 1, true);
    }

	public void checkConflict(Object obj) {
		int temp = _direction;
		_direction = CHECK_CONFLICT;
		replicate(obj);
		_direction = temp;
	}

	public void commit() {
        synchronized (_peerA.lock()) {
            synchronized (_peerB.lock()) {
        
        		_peerA.commit(_transA);
        		_peerB.commit(_transB);
        
                endReplication();
        
        		long versionA = _peerA.currentVersion();
        		long versionB = _peerB.currentVersion();
        
        		_record._version = (versionA > versionB) ? versionA :versionB;
                
                _peerA.raiseVersion(_record._version + 1);
                _peerB.raiseVersion(_record._version + 1);
        
        		_record.store(_peerA);
        		_record.store(_peerB);
            }
        }
	}

	private void endReplication() {
        
		_peerA.replicationCallState(Const4.NONE);
        _peerA._handlers.migrationConnection(null);
		_peerA._handlers.replication(null);
        
        _peerA.replicationCallState(Const4.NONE);
        _peerB._handlers.migrationConnection(null);
		_peerB._handlers.replication(null);
	}
    
    private int idInCaller(ObjectContainerBase caller, ObjectReference referenceA, ObjectReference referenceB){
        return (caller == _peerA) ? referenceA.getID() : referenceB.getID();
    }

	private int ignoreOrCheckConflict() {
		if (_direction == CHECK_CONFLICT) {
			return CHECK_CONFLICT;
		}
		return IGNORE;
	}
	
	private boolean isInConflict(long versionA, long versionB) {
		if(versionA > _record._version && versionB > _record._version) {
			return true;
		}
		if(versionB > _record._version && _direction == TO_B) {
			return true;
		}
		if(versionA > _record._version && _direction == TO_A) {
			return true;
		}
		return false;
	}

	private long lastSynchronization() {
		return _record._version;
	}
    
	public ObjectContainer peerA() {
		return (ObjectContainer)_peerA;
	}

	public ObjectContainer peerB() {
		return (ObjectContainer)_peerB;
	}
    
	public void replicate(Object obj) {

		// When there is an active replication process, the set() method
		// will call back to the #process() method in this class.

		// This detour is necessary, since #set() has to handle all cases
		// anyway, for members of the replicated object, especially the
		// prevention of endless loops in case of circular references.

		ObjectContainerBase container = _peerB;
		Transaction trans = _transB;

		if (_peerB.isStored(_transB, obj)) {
			if (!_peerA.isStored(_transA, obj)) {
				container = _peerA;
				trans = _transA;
			}
		}

		container.set(trans, obj);
	}

	public void rollback() {
		_peerA.rollback(_transA);
		_peerB.rollback(_transB);
		endReplication();
	}

	public void setDirection(ObjectContainer replicateFrom,
			ObjectContainer replicateTo) {
		if (replicateFrom == _peerA && replicateTo == _peerB) {
			_direction = TO_B;
		}
		if (replicateFrom == _peerB && replicateTo == _peerA) {
			_direction = TO_A;
		}
	}

	private void shareBinding(ObjectReference sourceReference, ObjectReference referenceA, Object objectA, ObjectReference referenceB, Object objectB) {
		if(sourceReference == null) {
			return;
		}
        if(objectA instanceof Db4oTypeImpl){
            if(! ((Db4oTypeImpl)objectA).canBind() ){
                return;
            }
        }
        
		if(sourceReference == referenceA) {
			_peerB.bind2(_transB, referenceB, objectA);
		}else {
			_peerA.bind2(_transA, referenceA, objectB);
		}
	}

	private int toA() {
		if (_direction == CHECK_CONFLICT) {
			return CHECK_CONFLICT;
		}
		if (_direction != TO_B) {
			return TO_A;
		}
		return IGNORE;
	}

	private int toB() {
		if (_direction == CHECK_CONFLICT) {
			return CHECK_CONFLICT;
		}
		if (_direction != TO_A) {
			return TO_B;
		}
		return IGNORE;
	}
    
	
	/**
	 * called by YapStream.set()
	 * @return id of reference in caller or 0 if not handled or -1
     * if #set() should stop processing because of a direction 
     * setting.
	 */
	public int tryToHandle(ObjectContainerBase caller, Object obj) {
        
        int notProcessed = 0;
        ObjectContainerBase other = null;
        ObjectReference sourceReference = null;
        
        if(caller == _peerA){
            other = _peerB;
            if(_direction == TO_B){
                notProcessed = -1;
            }
        }else{
            other = _peerA;
            if(_direction == TO_A){
                notProcessed = -1;
            }
        }
        
		synchronized (other._lock) {
            
			Object objectA = obj;
			Object objectB = obj;
			
			ObjectReference referenceA = _transA.referenceForObject(obj);
			ObjectReference referenceB = _transB.referenceForObject(obj);
			
			VirtualAttributes attA = null;
			VirtualAttributes attB = null;
			
			if (referenceA == null) {
				if(referenceB == null) {
					return notProcessed;
				}
				
				sourceReference = referenceB;
				
				attB = referenceB.virtualAttributes(_transB);
                if(attB == null){
                    return notProcessed;
                }
				
				HardObjectReference hardRef = _transA.getHardReferenceBySignature(attB.i_uuid,
						attB.i_database.i_signature);
				if (hardRef._object == null) {
					return notProcessed;
				}
				
				referenceA = hardRef._reference;
				objectA = hardRef._object;
				
				attA = referenceA.virtualAttributes(_transA);
			}else {
				
				attA = referenceA.virtualAttributes(_transA);
                if(attA == null){
                    return notProcessed;
                }
				
				if(referenceB == null) {
                    
					sourceReference = referenceA;
                    
					HardObjectReference hardRef = _transB.getHardReferenceBySignature(attA.i_uuid,
							attA.i_database.i_signature);
                    
					if (hardRef._object == null) {
						return notProcessed;
					}
                    
					referenceB =  hardRef._reference;
					objectB = hardRef._object;
                    
				}
				
				attB = referenceB.virtualAttributes(_transB);
			}
            
            if(attA == null || attB == null){
                return notProcessed;
            }
			
			if(objectA == objectB) {
				if(caller == _peerA && _direction == TO_B) {
					return -1;
				}
				if(caller == _peerB && _direction == TO_A) {
					return -1;
				}
				return idInCaller(caller, referenceA, referenceB);
			}
			
			_peerA.refresh(_transA, objectA, 1);
			_peerB.refresh(_transB, objectB, 1);
			
			if (attA.i_version <= _record._version
					&& attB.i_version <= _record._version) {

				if (_direction != CHECK_CONFLICT) {
					shareBinding(sourceReference, referenceA, objectA, referenceB, objectB);
				}
                return idInCaller(caller, referenceA, referenceB);
			}

			int direction = ignoreOrCheckConflict();

			if (isInConflict(attA.i_version, attB.i_version)) {
                
				Object prevailing = _conflictHandler.resolveConflict(this,
						objectA, objectB);

				if (prevailing == objectA) {
					direction = (_direction == TO_A) ? IGNORE : toB(); 
				}

				if (prevailing == objectB) {
					direction = (_direction == TO_B) ? IGNORE : toA();
				}

				if (direction == IGNORE) {
					return -1;
				}

			} else {
				direction = attB.i_version > _record._version ? toA(): toB();
			}

			if (direction == TO_A) {
				if (!referenceB.isActive()) {
					referenceB.activate(_transB, objectB, 1, false);
				}
                int idA = bindAndSet(_transA, _peerA, referenceA, objectB);
                if(caller == _peerA){
                    return idA;
                }
			}

			if (direction == TO_B) {
				if (!referenceA.isActive()) {
					referenceA.activate(_transA, objectA, 1, false);
				}
                int idB = bindAndSet(_transB, _peerB, referenceB, objectA);
                if(caller == _peerB){
                    return idB;
                }
			}

            return idInCaller(caller, referenceA, referenceB);
		}

	}
    
	public void whereModified(Query query) {
		query.descend(VirtualField.VERSION).constrain(
				new Long(lastSynchronization())).greater();
	}
 
}