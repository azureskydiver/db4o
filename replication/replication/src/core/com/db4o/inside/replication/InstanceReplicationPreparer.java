package com.db4o.inside.replication;

import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.TimeStampIdGenerator;
import com.db4o.inside.traversal.Traverser;
import com.db4o.inside.traversal.Visitor;
import com.db4o.reflect.ReflectClass;
import com.db4o.replication.ReplicationConflictException;
import com.db4o.replication.ReplicationEventListener;
import com.db4o.replication.ReplicationProvider;

class InstanceReplicationPreparer implements Visitor {

	private final ReplicationProviderInside _providerA;
	private final ReplicationProviderInside _providerB;
	private final ReplicationProvider _directionTo;
	private final ReplicationEventListener _listener;
	private final boolean _isReplicatingOnlyDeletions;
	private final long _lastReplicationVersion;
	private final Hashtable4 _uuidsProcessedInSession;
	private final Traverser _traverser;
	private final ReplicationReflector _reflector;
	private final CollectionHandler _collectionHandler;

	/**
	 * Purpose: handle circular references
	 * TODO Big Refactoring: Evolve this to handle ALL reference logic (!) and remove it from the providers. 
	 */
	private final Hashtable4 _objectsPreparedToReplicate = new Hashtable4(10000);
	/**
	 * key = object originated from one provider
	 * value = the counterpart ReplicationReference of the original object
	 */
	private Hashtable4 _counterpartRefsByOriginal = new Hashtable4(10000);
	
	private final ReplicationEventImpl _event;
	private final ObjectStateImpl _stateInA;
	private final ObjectStateImpl _stateInB;

	private Object _obj;
	private Object _referencingObject;
	private String _fieldName;	
	
	InstanceReplicationPreparer(ReplicationProviderInside providerA, ReplicationProviderInside providerB, ReplicationProvider directionTo, ReplicationEventListener listener, boolean isReplicatingOnlyDeletions, long lastReplicationVersion, Hashtable4 uuidsProcessedInSession, Traverser traverser, ReplicationReflector reflector, CollectionHandler collectionHandler) {
		_event = new ReplicationEventImpl();
		_stateInA = _event._stateInProviderA;
		_stateInB = _event._stateInProviderB;
		
		_providerA = providerA;
		_providerB = providerB;
		_directionTo = directionTo;
		_listener = listener;
		_isReplicatingOnlyDeletions = isReplicatingOnlyDeletions;
		_lastReplicationVersion = lastReplicationVersion;
		_uuidsProcessedInSession = uuidsProcessedInSession;
		_traverser = traverser;
		_reflector = reflector;
		_collectionHandler = collectionHandler;
	}


	public final boolean visit(Object obj) {
		if (_objectsPreparedToReplicate.get(obj) != null) return false;
		_objectsPreparedToReplicate.put(obj, obj);

		return prepareObjectToBeReplicated(obj, null, null);
	}

	
	private boolean prepareObjectToBeReplicated(Object obj, Object referencingObject, String fieldName) {
		//TODO Optimization: keep track of the peer we are traversing to avoid having to look in both.

		_obj = obj;
		_referencingObject = referencingObject;
		_fieldName = fieldName;

		ReplicationReference refA = _providerA.produceReference(_obj, _referencingObject, _fieldName);
		ReplicationReference refB = _providerB.produceReference(_obj, _referencingObject, _fieldName);

		if (refA == null && refB == null)
			throw new RuntimeException("" + _obj.getClass() + " " + _obj + " must be stored in one of the databases being replicated."); //FIXME: Use db4o's standard for throwing exceptions.
		if (refA != null && refB != null)
			throw new RuntimeException("" + _obj.getClass() + " " + _obj + " cannot be referenced by both databases being replicated."); //FIXME: Use db4o's standard for throwing exceptions.

		ReplicationProviderInside owner = refA == null ? _providerB : _providerA;
		ReplicationReference ownerRef = refA == null ? refB : refA;

		ReplicationProviderInside other = other(owner);

		Db4oUUID uuid = ownerRef.uuid();
		ReplicationReference otherRef = other.produceReferenceByUUID(uuid, _obj.getClass());

		if (refA == null)
			refA = otherRef;
		else
			refB = otherRef;

		//TODO for circular referenced object, otherRef should not be null in the subsequent pass.
		//But db4o always return null. A bug. check!
		if (otherRef == null) { //Object is only present in one ReplicationProvider. Missing in the other. Could have been deleted or never replicated.
			if (wasProcessed(uuid)) return false;
			markAsProcessed(uuid);

			long creationTime = ownerRef.uuid().getLongPart();

			if (creationTime > _lastReplicationVersion) { //if it was created after the last time two ReplicationProviders were replicated it has to be treated as new.
				if (_isReplicatingOnlyDeletions) return false;
				return handleNewObject(_obj, ownerRef, owner, other, _referencingObject, _fieldName, true, false);
			} else // if it was created before the last time two ReplicationProviders were replicated it has to be treated as deleted.
				return handleMissingObjectInOther(_obj, ownerRef, owner, other, _referencingObject, _fieldName);
		}

		if (_isReplicatingOnlyDeletions) return false;

		ownerRef.setCounterpart(otherRef.object());
		if (wasProcessed(uuid)) return false;  //Has to be done AFTER the counterpart is set because object yet to be replicated might reference the current one, replicated previously.
		markAsProcessed(uuid);

		Object objectA = refA.object();
		Object objectB = refB.object();

		boolean changedInA = _providerA.wasModifiedSinceLastReplication(refA);
		//System.out.println("changedInA = " + changedInA);
		boolean changedInB = _providerB.wasModifiedSinceLastReplication(refB);
		//System.out.println("changedInB = " + changedInB);

		if (!changedInA && !changedInB) return false;

		boolean conflict = false;
		if (changedInA && changedInB) conflict = true;
		if (changedInA && _directionTo == _providerA) conflict = true;
		if (changedInB && _directionTo == _providerB) conflict = true;

		Object prevailing = _obj;

		_providerA.activate(objectA);
		_providerB.activate(objectB);

		_event.resetAction();
		_event._isConflict = conflict;

		_event._creationDate = TimeStampIdGenerator.idToMilliseconds(uuid.getLongPart());

		_stateInA.setAll(objectA, false, changedInA, TimeStampIdGenerator.idToMilliseconds(ownerRef.version()));
		_stateInB.setAll(objectB, false, changedInB, TimeStampIdGenerator.idToMilliseconds(otherRef.version()));
		_listener.onReplicate(_event);

		if (conflict) {
			if (!_event._actionWasChosen) throwReplicationConflictException();
			if (_event._actionChosenState == null) return false;
			if (_event._actionChosenState == _stateInA) prevailing = objectA;
			if (_event._actionChosenState == _stateInB) prevailing = objectB;
		} else {
			if (_event._actionWasChosen) {
				if (_event._actionChosenState == _stateInA) prevailing = objectA;
				if (_event._actionChosenState == _stateInB) prevailing = objectB;
				if (_event._actionChosenState == null) return false;
			} else {
				if (changedInA) prevailing = objectA;
				if (changedInB) prevailing = objectB;
			}
		}

		ReplicationProviderInside prevailingPeer = prevailing == objectA ? _providerA : _providerB;
		if (_directionTo == prevailingPeer) return false;

		if (!conflict)
			prevailingPeer.activate(prevailing); //Already activated if there was a conflict.

		if (prevailing != _obj) {
			otherRef.setCounterpart(_obj);
			otherRef.markForReplicating();
			markAsNotProcessed(uuid);
			_traverser.extendTraversalTo(prevailing); //Now we start traversing objects on the other peer! Is that cool or what? ;)
		} else {
			ownerRef.markForReplicating();
		}

		return !_event._actionShouldStopTraversal;
	}


	private void markAsNotProcessed(Db4oUUID uuid) {
		_uuidsProcessedInSession.remove(uuid);
	}


	private void markAsProcessed(Db4oUUID uuid) {
		if (_uuidsProcessedInSession.get(uuid) != null) throw new RuntimeException("illegal state");

		_uuidsProcessedInSession.put(uuid, uuid); //Using this Hashtable4 as a Set.
	}


	private boolean wasProcessed(Db4oUUID uuid) {
		return _uuidsProcessedInSession.get(uuid) != null;
	}


	private ReplicationProviderInside other(ReplicationProviderInside peer) {
		return peer == _providerA ? _providerB : _providerA;
	}


	private boolean handleMissingObjectInOther(Object obj, ReplicationReference ownerRef,
			ReplicationProviderInside owner, ReplicationProviderInside other,
			Object referencingObject, String fieldName) {
		boolean isConflict = false;
		boolean wasModified = owner.wasModifiedSinceLastReplication(ownerRef);
		if (wasModified) isConflict = true;
		if (_directionTo == other) isConflict = true;

		Object prevailing = null; //by default, deletion prevails
		if (isConflict) owner.activate(obj);

		_event.resetAction();
		_event._isConflict = isConflict;

		_event._creationDate = TimeStampIdGenerator.idToMilliseconds(ownerRef.uuid().getLongPart());
		long modificationDate = TimeStampIdGenerator.idToMilliseconds(ownerRef.version());

		if (owner == _providerA) {
			_stateInA.setAll(obj, false, wasModified, modificationDate);
			_stateInB.setAll(null, false, false, -1);
		} else { //owner == _providerB
			_stateInA.setAll(null, false, false, -1);
			_stateInB.setAll(obj, false, wasModified, modificationDate);
		}

		_listener.onReplicate(_event);

		if (isConflict && !_event._actionWasChosen) throwReplicationConflictException();

		if (_event._actionWasChosen) {
			if (_event._actionChosenState == null) return false;
			if (_event._actionChosenState == _stateInA) prevailing = _stateInA.getObject();
			if (_event._actionChosenState == _stateInB) prevailing = _stateInB.getObject();
		}

		if (prevailing == null) { //Deletion has prevailed.
			if (_directionTo == other) return false;
			ownerRef.markForDeleting();
			return !_event._actionShouldStopTraversal;
		}

		boolean needsToBeActivated = !isConflict; //Already activated if there was a conflict.
		return handleNewObject(obj, ownerRef, owner, other, referencingObject, fieldName, needsToBeActivated, true);
	}


	private boolean handleNewObject(Object obj, ReplicationReference ownerRef, ReplicationProviderInside owner,
			ReplicationProviderInside other, Object referencingObject, String fieldName, boolean needsToBeActivated, boolean listenerAlreadyNotified) {
		if (_directionTo == owner) return false;
	
		if (needsToBeActivated) owner.activate(obj);
	
		if (!listenerAlreadyNotified) {
			_event.resetAction();
			_event._isConflict = false;
			_event._creationDate = TimeStampIdGenerator.idToMilliseconds(ownerRef.uuid().getLongPart());
	
			if (owner == _providerA) {
				_stateInA.setAll(obj, true, false, -1);
				_stateInB.setAll(null, false, false, -1);
			} else {
				_stateInA.setAll(null, false, false, -1);
				_stateInB.setAll(obj, true, false, -1);
			}
	
			_listener.onReplicate(_event);
	
			if (_event._actionWasChosen) {
				if (_event._actionChosenState == null) return false;
				if (_event._actionChosenState.getObject() != obj) return false;
			}
		}
		
		Object counterpart = emptyClone(owner, obj);
	
		ownerRef.setCounterpart(counterpart);
		ownerRef.markForReplicating();
	
		ReplicationReference otherRef = other.referenceNewObject(counterpart, ownerRef, getCounterpartRef(referencingObject), fieldName);
	
		putCounterpartRef(obj, otherRef);
	
		if (_event._actionShouldStopTraversal) return false;
	
		return true;
	}


	private void throwReplicationConflictException() {
		throw new ReplicationConflictException("A replication conflict ocurred and the ReplicationEventListener, if any, did not choose which state should override the other.");
	}


	private Object emptyClone(ReplicationProviderInside sourceProvider, Object obj) {
		if (obj == null) return null;
		ReflectClass claxx = _reflector.forObject(obj);
//		if (claxx.isSecondClass()) return obj;
		if (claxx.isSecondClass()) throw new RuntimeException("IllegalState");
//		if (claxx.isArray()) return arrayClone(obj, claxx, sourceProvider); //Copy arrayClone() from GenericReplicationSession if necessary.
		if (claxx.isArray())  throw new RuntimeException("IllegalState"); //Copy arrayClone() from GenericReplicationSession if necessary.
		if (_collectionHandler.canHandle(claxx)) {
			return collectionClone(obj, claxx);
		}
		claxx.skipConstructor(true); // FIXME This is ridiculously slow to do every time. Should ALWAYS be done automatically in the reflector.
		Object result = claxx.newInstance();
		if (result == null)
			throw new RuntimeException("Unable to create a new instance of " + obj.getClass()); //FIXME Use db4o's standard for throwing exceptions.
		return result;
	}

	
	private Object collectionClone(Object original, ReflectClass claxx) {
		return _collectionHandler.emptyClone(original, claxx);
	}
	

	private ReplicationReference getCounterpartRef(Object original) {
		return (ReplicationReference) _counterpartRefsByOriginal.get(original);
	}


	private void putCounterpartRef(Object obj, ReplicationReference otherRef) {
		if (_counterpartRefsByOriginal.get(obj) != null) throw new RuntimeException("illegal state");
		_counterpartRefsByOriginal.put(obj, otherRef);
	}

	
}
