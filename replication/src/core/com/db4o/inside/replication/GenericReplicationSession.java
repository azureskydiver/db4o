package com.db4o.inside.replication;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.traversal.*;
import com.db4o.reflect.*;
import com.db4o.replication.*;

public class GenericReplicationSession implements ReplicationSession {


	private final ReplicationReflector _reflector;

	private final CollectionHandler _collectionHandler;

	private ReplicationProviderInside _peerA;
	private ReplicationProviderInside _peerB;

	private ReplicationProvider _directionTo; //null means bidirectional replication.

	private final ReplicationEventListener _listener;
	private final ReplicationEventImpl _event = new ReplicationEventImpl();
	private ObjectStateImpl _stateInA = _event._stateInProviderA;
	private ObjectStateImpl _stateInB = _event._stateInProviderB;

	private final Traverser _traverser;

	private Hashtable4 _processedUuids = new Hashtable4(1000);


	/**
	 * key = object originated from one provider
	 * value = the counterpart ReplicationReference of the original object
	 */
	private Hashtable4 _counterpartRefsByOriginal = new Hashtable4(1000);


	public GenericReplicationSession(ReplicationProvider providerA, ReplicationProvider providerB, ReplicationEventListener listener) {

		_reflector = ReplicationReflector.getInstance();
		_collectionHandler = new CollectionHandlerImpl(_reflector.reflector());
		_traverser = new ReplicationTraverser(_reflector.reflector(), _collectionHandler);

		_peerA = (ReplicationProviderInside) providerA;
		_peerB = (ReplicationProviderInside) providerB;
		_listener = listener;

		synchronized (_peerA.getMonitor()) {
			synchronized (_peerB.getMonitor()) {
				_peerA.startReplicationTransaction(_peerB.getSignature());
				_peerB.startReplicationTransaction(_peerA.getSignature());
			}
		}
	}

	public GenericReplicationSession(ReplicationProviderInside _peerA, ReplicationProviderInside _peerB) {
		this(_peerA, _peerB, new DefaultReplicationEventListener());
	}

	public void replicate(Object root) {
		//System.out.println("GenericReplicationSession.replicate");
		try {
			activateGraphToBeReplicated(root);

			copyStateAcross(_peerA);
			copyStateAcross(_peerB);

			storeChangedObjectsIn(_peerA);
			storeChangedObjectsIn(_peerB);
		} finally {
			_peerA.clearAllReferences();
			_peerB.clearAllReferences();
		}
	}

//	public void replicate(Db4oUUID uuid) {
//
//		Object objA = _peerA.getObject(uuid);
//		Object objB = _peerB.getObject(uuid);
//
//		ReplicationReference refA = _peerA.produceReferenceByUUID(uuid, null);
//		ReplicationReference refB = _peerB.produceReferenceByUUID(uuid, null);
//
//		if (refA != null && refB != null)
//			throw new RuntimeException("Object with given UUID must have been deleted in at least one of the databases being replicated.");
//
//		if (refA == null && refB == null) return;  //Deleted in both - Do nothing
//
//		Object obj = refA == null
//			? refB.object()
//			: refA.object();
//
//		replicate(obj);
//
//		//Deleted in one
//			//No conflict - replicate (check direction)
//			//Conflict (deletion in one and object in the other)
//				//Resolver return null - do nothing
//				//Deletion prevails - replicate deletion (check direction)
//				//Object prevails - undo deletion (check direction)
//
//
//		//  A       1' ->  2' -> 3    (changed)
//		//  B       []  ->  []  -> 3    (deleted)
//
//		//Results:
//		//TO_A:   []  -> []  -> 3
//		//TO_B:    1' -> 2' -> 3
//
//
//
//		//  A       1* ->  2* -> 3    (new)
//		//  B           ->     -> 3    (non-existant)
//
//
//
//	}


	private void activateGraphToBeReplicated(Object root) {
		_traverser.traverseGraph(root, new Visitor() {
			public boolean visit(Object object) {
				if (object instanceof TraversedField) {
					final TraversedField traversedField = ((TraversedField) object);
					return activateObjectToBeReplicated(traversedField.getValue(), traversedField.getReferencingObject(), traversedField.getName());
				} else {
					return activateObjectToBeReplicated(object, null, null);
				}
			}
		});
	}

	private void copyStateAcross(final ReplicationProviderInside sourceProvider) {
		if (_directionTo == sourceProvider) return;
		sourceProvider.visitCachedReferences(new Visitor4() {
			public void visit(Object obj) {
				copyStateAcross((ReplicationReference) obj, sourceProvider);
			}
		});
	}

	private void copyStateAcross(ReplicationReference sourceRef, ReplicationProviderInside sourceProvider) {
		if (!sourceRef.isMarkedForReplicating()) return;
		copyStateAcross(sourceRef.object(), sourceRef.counterpart(), sourceProvider);
	}

	private void copyStateAcross(Object source, Object dest, final ReplicationProviderInside sourceProvider) {
		ReflectClass claxx = _reflector.forObject(source);
		if (_collectionHandler.canHandle(claxx)) {
			_collectionHandler.copyState(source, dest, new CounterpartFinder() {
				public Object findCounterpart(Object original) {
					return GenericReplicationSession.this.findCounterpart(original, sourceProvider);
				}
			});
			return;
		}
		copyFieldValuesAcross(source, dest, claxx, sourceProvider);
	}

	private void storeChangedObjectsIn(final ReplicationProviderInside destination) {
		final ReplicationProviderInside source = other(destination);
		if (_directionTo == source) return;

		destination.visitCachedReferences(new Visitor4() {
			public void visit(Object obj) {
				deleteInDestination((ReplicationReference) obj, destination);
			}
		});

		source.visitCachedReferences(new Visitor4() {
			public void visit(Object obj) {
				storeChangedCounterpartInDestination((ReplicationReference) obj, destination);
			}
		});
	}

	private void deleteInDestination(ReplicationReference reference, ReplicationProviderInside destination) {
		if (!reference.isMarkedForDeleting()) return;
		destination.replicateDeletion(reference);
	}

	private void storeChangedCounterpartInDestination(ReplicationReference reference, ReplicationProviderInside destination) {
		if (!reference.isMarkedForReplicating()) return;
		destination.storeReplica(reference.counterpart());
	}

	ReplicationReference getCounterpartRef(Object original) {
		return (ReplicationReference) _counterpartRefsByOriginal.get(original);
	}

	private boolean activateObjectToBeReplicated(Object obj, Object referencingObject, String fieldName) { //TODO Optimization: keep track of the peer we are traversing to avoid having to look in both.
		if (_peerA.hasReplicationReferenceAlready(obj)) return false;
		if (_peerB.hasReplicationReferenceAlready(obj)) return false;

		ReplicationReference refA = _peerA.produceReference(obj, referencingObject, fieldName);
		ReplicationReference refB = _peerB.produceReference(obj, referencingObject, fieldName);

		if (refA == null && refB == null)
			throw new RuntimeException("" + obj.getClass() + " " + obj + " must be stored in one of the databases being replicated."); //FIXME: Use db4o's standard for throwing exceptions.
		if (refA != null && refB != null)
			throw new RuntimeException("" + obj.getClass() + " " + obj + " cannot be referenced by both databases being replicated."); //FIXME: Use db4o's standard for throwing exceptions.

		ReplicationProviderInside owner = refA == null ? _peerB : _peerA;
		ReplicationReference ownerRef = refA == null ? refB : refA;

		ReplicationProviderInside other = other(owner);

		Db4oUUID uuid = ownerRef.uuid();
		ReplicationReference otherRef = other.produceReferenceByUUID(uuid, obj.getClass());
		if (1==1) throw new RuntimeException();

		if (refA == null)
			refA = otherRef;
		else
			refB = otherRef;

		if (otherRef == null) {
            markAsProcessed(uuid);

            if (other.wasDeletedSinceLastReplication(uuid))
				return handleDeletionInOther(obj, ownerRef, owner, other, referencingObject, fieldName);

			return handleNewObject(obj, ownerRef, owner, other, referencingObject, fieldName, true);
		}

		ownerRef.setCounterpart(otherRef.object());

        if (wasProcessed(uuid)) return false;
        markAsProcessed(uuid);

		Object objectA = refA.object();
		Object objectB = refB.object();

		boolean changedInA = _peerA.wasModifiedSinceLastReplication(refA);
		boolean changedInB = _peerB.wasModifiedSinceLastReplication(refB);

		if (!changedInA && !changedInB) return false;

		boolean conflict = false;
		if (changedInA && changedInB) conflict = true;
		if (changedInA && _directionTo == _peerA) conflict = true;
		if (changedInB && _directionTo == _peerB) conflict = true;

		Object prevailing = obj;
		if (conflict) {
			_peerA.activate(objectA);
			_peerB.activate(objectB);

			_event.resetAction();
			_event._isConflict = true;
			_stateInA.setAll(objectA, false, changedInA, false);
			_stateInB.setAll(objectB, false, changedInB, false);
			_listener.onReplicate(_event);

			if (!_event._actionWasChosen) throwReplicationConflictException();
			if (_event._actionChosen == null) return false;
			if (_event._actionChosen == _stateInA) prevailing = objectA;
			if (_event._actionChosen == _stateInB) prevailing = objectB;
		}

		ReplicationProviderInside prevailingPeer = prevailing == objectA ? _peerA : _peerB;
		if (_directionTo == prevailingPeer) return false;

		if (!conflict)
			prevailingPeer.activate(prevailing); //Already activated if there was a conflict.

		if (prevailing != obj) {
			otherRef.setCounterpart(obj);
			otherRef.markForReplicating();
			_traverser.extendTraversalTo(prevailing); //Now we start traversing objects on the other peer! Is that cool or what? ;)
			return false;
		}

		ownerRef.markForReplicating();
		return true;
	}

	private void throwReplicationConflictException() {
		throw new ReplicationConflictException("A replication conflict ocurred and the ReplicationEventListener, if any, did not choose which state should override the other.");
	}


    private boolean wasProcessed(Db4oUUID uuid) {
        return _processedUuids.get(uuid) != null;
    }

    private void markAsProcessed(Db4oUUID uuid) {
        _processedUuids.put(uuid, uuid); //Using this Hashtable4 as a Set.
    }


	private boolean handleDeletionInOther(Object obj, ReplicationReference ownerRef, ReplicationProviderInside owner, ReplicationProviderInside other, Object referencingObject, String fieldName) {

		boolean isConflict = false;
		boolean wasModified = owner.wasModifiedSinceLastReplication(ownerRef);
		if (wasModified) isConflict = true;
		if (_directionTo == other) isConflict = true;

		Object prevailing = null;
		if (isConflict) {
			owner.activate(obj);

			_event.resetAction();
			_event._isConflict = true;
			if (owner == _peerA) {
				_stateInA.setAll(obj, false, wasModified, false);
				_stateInB.setAll(null, false, false, true);
			} else { //owner == _peerB
				_stateInA.setAll(null, false, false, true);
				_stateInB.setAll(obj, false, wasModified, false);
			}
			_listener.onReplicate(_event);

			if (!_event._actionWasChosen)
				throwReplicationConflictException();
			if (_event._actionChosen == null) return false;
			if (_event._actionChosen == _stateInA) prevailing = _stateInA.getObject();
			if (_event._actionChosen == _stateInB) prevailing = _stateInB.getObject();
		}

		if (prevailing == null) { //Deletion has prevailed.
			if (_directionTo == other) return false;
			ownerRef.markForDeleting();
			return true;
		}

		boolean needsToBeActivated = !isConflict; //Already activated if there was a conflict.
		return handleNewObject(obj, ownerRef, owner, other, referencingObject, fieldName, needsToBeActivated);

	}


	private boolean handleNewObject(Object obj, ReplicationReference ownerRef, ReplicationProviderInside owner, ReplicationProviderInside other, Object referencingObject, String fieldName, boolean needsToBeActivated) {
		//System.out.println("handleNewObject = " + obj);
		if (_directionTo == owner) return false;

		if (needsToBeActivated) owner.activate(obj);

		Object counterpart = emptyClone(owner, obj);

		ownerRef.setCounterpart(counterpart);
		ownerRef.markForReplicating();

		ReplicationReference otherRef = other.referenceNewObject(counterpart, ownerRef, getCounterpartRef(referencingObject), fieldName);
		_counterpartRefsByOriginal.put(obj, otherRef);

		// TODO: We might not need counterpart in otherRef. Check.
		otherRef.setCounterpart(obj);
        
		return true;
	}

	private Object emptyClone(ReplicationProviderInside sourceProvider, Object obj) {
		if (obj == null) return null;
		ReflectClass claxx = _reflector.forObject(obj);
		if (claxx.isSecondClass()) return obj;
		if (claxx.isArray()) return arrayClone(obj, claxx, sourceProvider);
		if (_collectionHandler.canHandle(claxx)) {
			return collectionClone(obj, claxx);
		}
		claxx.skipConstructor(true); // FIXME This is ridiculously slow to do every time. Should ALWAYS be done automatically in the reflector.
		Object result = claxx.newInstance();
		if (result == null)
			throw new RuntimeException("Unable to create a new instance of " + obj.getClass()); //FIXME Use db4o's standard of throwing exceptions.
		return result;
	}

	private void copyFieldValuesAcross(Object src, Object dest, ReflectClass claxx, ReplicationProviderInside sourceProvider) {
		ReflectField[] fields;

		fields = claxx.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			ReflectField field = fields[i];
			if (field.isStatic()) continue;
			if (field.isTransient()) continue;
			field.setAccessible(); //TODO Optimization: Do this in the field constructor;
			Object value = field.get(src);
			field.set(dest, findCounterpart(value, sourceProvider));
		}

		ReflectClass superclass = claxx.getSuperclass();
		if (superclass == null) return;
		copyFieldValuesAcross(src, dest, superclass, sourceProvider);
	}

	public Object findCounterpart(Object value, ReplicationProviderInside sourceProvider) {
		if (value == null) return null;
		ReflectClass claxx = _reflector.forObject(value);
		if (claxx.isArray()) return arrayClone(value, claxx, sourceProvider);
		if (claxx.isSecondClass()) return value;

		//if value is a Collection, result should be found by passing in just the value
		Object result = sourceProvider.produceReference(value, null, null).counterpart();
		if (result == null)
			throw new NullPointerException("unable to find the counterpart of " + value + " of class " + value.getClass());
		return result;
	}

	private Object collectionClone(Object original, ReflectClass claxx) {
		return _collectionHandler.emptyClone(original, claxx);
	}

	private Object arrayClone(Object original, ReflectClass claxx, ReplicationProviderInside sourceProvider) {
		ReflectClass componentType = _reflector.getComponentType(claxx);
		int[] dimensions = _reflector.arrayDimensions(original);
		Object result = _reflector.newArrayInstance(componentType, dimensions);
		Object[] flatContents = _reflector.arrayContents(original); //TODO Optimize: Copy the structure without flattening. Do this in ReflectArray.
		if (!claxx.isSecondClass())
			replaceWithCounterparts(flatContents, sourceProvider);
		_reflector.arrayShape(flatContents, 0, result, dimensions, 0);
		return result;
	}

	private void replaceWithCounterparts(Object[] objects, ReplicationProviderInside sourceProvider) {
		for (int i = 0; i < objects.length; i++) {
			Object object = objects[i];
			if (object == null) continue;

			ReplicationReference replicationReference = sourceProvider.produceReference(object, null, null);

			if (replicationReference == null)
				throw new RuntimeException(sourceProvider + " cannot find ref for " + object);

			objects[i] = replicationReference.counterpart();
		}
	}

	private ReplicationProviderInside other(ReplicationProviderInside peer) {
		return peer == _peerA ? _peerB : _peerA;
	}

	public void checkConflict(Object root) {
		try {
			activateGraphToBeReplicated(root);
		} finally {
			_peerA.clearAllReferences();
			_peerB.clearAllReferences();
		}
	}

	public ReplicationProvider providerA() {
		return _peerA;
	}

	public ReplicationProvider providerB() {
		return _peerB;
	}

	public void close() {
		_peerA.destroy();
		_peerB.destroy();

		_peerA = null;
		_peerB = null;
		_counterpartRefsByOriginal = null;
		_processedUuids = null;
	}

	public void commit() {
		synchronized (_peerA.getMonitor()) {
			synchronized (_peerB.getMonitor()) {

				long maxVersion = _peerA.getCurrentVersion() > _peerB.getCurrentVersion()
						? _peerA.getCurrentVersion() : _peerB.getCurrentVersion();

				_peerA.syncVersionWithPeer(maxVersion);
				_peerB.syncVersionWithPeer(maxVersion);

				maxVersion ++;

				_peerA.commitReplicationTransaction(maxVersion);
				_peerB.commitReplicationTransaction(maxVersion);
			}

		}
	}

	public void rollback() {
		_peerA.rollbackReplication();
		_peerB.rollbackReplication();
	}

	public void setDirection(ReplicationProvider replicateFrom, ReplicationProvider replicateTo) {
		if (replicateFrom == _peerA && replicateTo == _peerB)
			_directionTo = _peerB;
		if (replicateFrom == _peerB && replicateTo == _peerA)
			_directionTo = _peerA;
	}

}
