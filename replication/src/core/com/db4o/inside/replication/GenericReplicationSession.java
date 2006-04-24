package com.db4o.inside.replication;

import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.TimeStampIdGenerator;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.traversal.TraversedField;
import com.db4o.inside.traversal.Traverser;
import com.db4o.inside.traversal.Visitor;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.replication.ReplicationConflictException;
import com.db4o.replication.ReplicationEventListener;
import com.db4o.replication.ReplicationProvider;
import com.db4o.replication.ReplicationSession;

public final class GenericReplicationSession implements ReplicationSession {
	private static final int SIZE = 10000;

	private final ReplicationReflector _reflector;

	private final CollectionHandler _collectionHandler;

	private ReplicationProviderInside _providerA;

	private ReplicationProviderInside _providerB;

	private ReplicationProvider _directionTo; //null means bidirectional replication.

	private final ReplicationEventListener _listener;

	private final ReplicationEventImpl _event = new ReplicationEventImpl();

	private final ObjectStateImpl _stateInA = _event._stateInProviderA;

	private final ObjectStateImpl _stateInB = _event._stateInProviderB;

	private final Traverser _traverser;

	private long _lastReplicationVersion;

	private Hashtable4 _processedUuidsWithinSession;

	/**
	 * Purpose: handle circular references
	 * TODO Big Refactoring: Evolve this to handle ALL reference logic (!) and remove it from the providers. 
	 */
	private Hashtable4 _processedObjectsWithinReplicate;

	/**
	 * key = object originated from one provider
	 * value = the counterpart ReplicationReference of the original object
	 */
	private Hashtable4 _counterpartRefsByOriginal;

	public GenericReplicationSession(ReplicationProviderInside _peerA, ReplicationProviderInside _peerB) {
		this(_peerA, _peerB, new DefaultReplicationEventListener());
	}

	public GenericReplicationSession(ReplicationProvider providerA, ReplicationProvider providerB, ReplicationEventListener listener) {
		_reflector = ReplicationReflector.getInstance();
		_collectionHandler = new CollectionHandlerImpl(_reflector.reflector());
		_traverser = new ReplicationTraverser(_reflector.reflector(), _collectionHandler);

		_providerA = (ReplicationProviderInside) providerA;
		_providerB = (ReplicationProviderInside) providerB;
		_listener = listener;

		synchronized (_providerA.getMonitor()) {
			synchronized (_providerB.getMonitor()) {
				_providerA.startReplicationTransaction(_providerB.getSignature());
				_providerB.startReplicationTransaction(_providerA.getSignature());

				if (_providerA.getLastReplicationVersion() != _providerB.getLastReplicationVersion())
					throw new RuntimeException("Version numbers must be the same");

				_lastReplicationVersion = _providerA.getLastReplicationVersion();
			}
		}

		resetCounterpartRefsByOriginal();
		resetProcessedObjectsInThisReplicateCall();
		resetProcessedUuids();
	}

	public final void checkConflict(Object root) {
		try {
			activateGraphToBeReplicated(root);
		} finally {
			_providerA.clearAllReferences();
			_providerB.clearAllReferences();
		}
	}

	public final void close() {
		_providerA.destroy();
		_providerB.destroy();

		_providerA = null;
		_providerB = null;
		_counterpartRefsByOriginal = null;
		_processedUuidsWithinSession = null;
	}

	public final void commit() {
		synchronized (_providerA.getMonitor()) {
			synchronized (_providerB.getMonitor()) {
				long maxVersion = _providerA.getCurrentVersion() > _providerB.getCurrentVersion()
						? _providerA.getCurrentVersion() : _providerB.getCurrentVersion();

				_providerA.syncVersionWithPeer(maxVersion);
				_providerB.syncVersionWithPeer(maxVersion);

				maxVersion ++;

				_providerA.commitReplicationTransaction(maxVersion);
				_providerB.commitReplicationTransaction(maxVersion);
			}
		}
	}

	public final ReplicationProvider providerA() {
		return _providerA;
	}

	public final ReplicationProvider providerB() {
		return _providerB;
	}

	public final void replicate(Object root) {
		try {
			activateGraphToBeReplicated(root);

			copyStateAcross(_providerA);
			copyStateAcross(_providerB);

			storeChangedObjectsIn(_providerA);
			storeChangedObjectsIn(_providerB);
		} finally {
			_providerA.clearAllReferences();
			_providerB.clearAllReferences();
			resetProcessedObjectsInThisReplicateCall();
			resetCounterpartRefsByOriginal();
		}
	}

	public final void replicateDeleted(Class extent) {
		  throw new UnsupportedOperationException();
	}

	public final void rollback() {
		_providerA.rollbackReplication();
		_providerB.rollbackReplication();
	}

	public final void setDirection(ReplicationProvider replicateFrom, ReplicationProvider replicateTo) {
		if (replicateFrom == _providerA && replicateTo == _providerB)
			_directionTo = _providerB;
		if (replicateFrom == _providerB && replicateTo == _providerA)
			_directionTo = _providerA;
	}

	private void activateGraphToBeReplicated(Object root) {
		_traverser.traverseGraph(root, new ReplicationVisitor());
	}

	private boolean activateObjectToBeReplicated(Object obj, Object referencingObject, String fieldName) {
		//TODO Optimization: keep track of the peer we are traversing to avoid having to look in both.

		if (_processedObjectsWithinReplicate.get(obj)!=null) return false;
		_processedObjectsWithinReplicate.put(obj, obj);

		ReplicationReference refA = _providerA.produceReference(obj, referencingObject, fieldName);
		ReplicationReference refB = _providerB.produceReference(obj, referencingObject, fieldName);

		if (refA == null && refB == null)
			throw new RuntimeException("" + obj.getClass() + " " + obj + " must be stored in one of the databases being replicated."); //FIXME: Use db4o's standard for throwing exceptions.
		if (refA != null && refB != null)
			throw new RuntimeException("" + obj.getClass() + " " + obj + " cannot be referenced by both databases being replicated."); //FIXME: Use db4o's standard for throwing exceptions.

		ReplicationProviderInside owner = refA == null ? _providerB : _providerA;
		ReplicationReference ownerRef = refA == null ? refB : refA;

		ReplicationProviderInside other = other(owner);

		Db4oUUID uuid = ownerRef.uuid();
		ReplicationReference otherRef = other.produceReferenceByUUID(uuid, obj.getClass());

		if (refA == null)
			refA = otherRef;
		else
			refB = otherRef;

		//TODO for circular referenced object, otherRef should not be null in the subsequent pass.
		//But db4o always return null. A bug. check!
		if (otherRef == null) { //If an object is only present in one ReplicationProvider
			markAsProcessed(uuid);

			long creationTime = ownerRef.uuid().getLongPart();

			if (creationTime > _lastReplicationVersion) //if it was created after the last time two ReplicationProviders were replicated it has to be treated as new.
				return handleNewObject(obj, ownerRef, owner, other, referencingObject, fieldName, true);
			else // if it was created before the last time two ReplicationProviders were replicated it has to be treated as deleted.
				return handleDeletionInOther(obj, ownerRef, owner, other, referencingObject, fieldName);
		}

		ownerRef.setCounterpart(otherRef.object());
		if (wasProcessed(uuid)) return false;  //Has to be done AFTER the counterpart is set.
		markAsProcessed(uuid);

		Object objectA = refA.object();
		Object objectB = refB.object();

		boolean changedInA = _providerA.wasModifiedSinceLastReplication(refA);
		boolean changedInB = _providerB.wasModifiedSinceLastReplication(refB);

		if (!changedInA && !changedInB) return false;

		boolean conflict = false;
		if (changedInA && changedInB) conflict = true;
		if (changedInA && _directionTo == _providerA) conflict = true;
		if (changedInB && _directionTo == _providerB) conflict = true;

		Object prevailing = obj;

		_providerA.activate(objectA);
		_providerB.activate(objectB);

		_event.resetAction();
		_event._isConflict = conflict;

		_event._creationDate = TimeStampIdGenerator.idToMilliseconds(uuid.getLongPart());

		_stateInA.setAll(objectA, false, changedInA, TimeStampIdGenerator.idToMilliseconds(ownerRef.version()));
		_stateInB.setAll(objectB, false, changedInB, TimeStampIdGenerator.idToMilliseconds(otherRef.version()));
		_listener.onReplicate(_event);

		if (_event._actionShouldStopTraversal)
			return false;

		if (conflict) {
			if (!_event._actionWasChosen) throwReplicationConflictException();
			if (_event._actionChosen == null) return false;
			if (_event._actionChosen == _stateInA) prevailing = objectA;
			if (_event._actionChosen == _stateInB) prevailing = objectB;
		} else {
			if (_event._actionWasChosen) {
				if (_event._actionChosen == _stateInA) prevailing = objectA;
				if (_event._actionChosen == _stateInB) prevailing = objectB;
				if (_event._actionChosen == null) return false;
			} else {
				if (changedInA) prevailing = objectA;
				if (changedInB) prevailing = objectB;
			}
		}

		ReplicationProviderInside prevailingPeer = prevailing == objectA ? _providerA : _providerB;
		if (_directionTo == prevailingPeer) return false;

		if (!conflict)
			prevailingPeer.activate(prevailing); //Already activated if there was a conflict.

		if (prevailing != obj) {
			otherRef.setCounterpart(obj);
			otherRef.markForReplicating();
			markAsNotProcessed(uuid);
			_traverser.extendTraversalTo(prevailing); //Now we start traversing objects on the other peer! Is that cool or what? ;)
			return false;
		}

		ownerRef.markForReplicating();
		return true;
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

	private Object collectionClone(Object original, ReflectClass claxx) {
		return _collectionHandler.emptyClone(original, claxx);
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

	private Object findCounterpart(Object value, ReplicationProviderInside sourceProvider) {
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

	private ReplicationReference getCounterpartRef(Object original) {
		return (ReplicationReference) _counterpartRefsByOriginal.get(original);
	}

	private boolean handleDeletionInOther(Object obj, ReplicationReference ownerRef,
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

		if (_event._actionShouldStopTraversal) return false;

		if (isConflict && !_event._actionWasChosen) throwReplicationConflictException();

		if (_event._actionWasChosen) {
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

	private boolean handleNewObject(Object obj, ReplicationReference ownerRef, ReplicationProviderInside owner,
			ReplicationProviderInside other, Object referencingObject, String fieldName, boolean needsToBeActivated) {
		if (_directionTo == owner) return false;

		if (needsToBeActivated) owner.activate(obj);

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

		if (_event._actionShouldStopTraversal)
			return false;

		if (_event._actionWasChosen)
			if (_event._actionChosen.getObject() != obj)
				throw new RuntimeException("You can only choose the new object or stop traversal");

		Object counterpart = emptyClone(owner, obj);

		ownerRef.setCounterpart(counterpart);
		ownerRef.markForReplicating();

		ReplicationReference otherRef = other.referenceNewObject(counterpart, ownerRef, getCounterpartRef(referencingObject), fieldName);

		putCounterpartRef(obj, otherRef);

		return true;
	}

	private void markAsNotProcessed(Db4oUUID uuid) {
		_processedUuidsWithinSession.remove(uuid);
	}

	private void markAsProcessed(Db4oUUID uuid) {
		if (_processedUuidsWithinSession.get(uuid) == null)
			_processedUuidsWithinSession.put(uuid, uuid); //Using this Hashtable4 as a Set.
		else
			throw new RuntimeException("should be unreachable");
	}

	private ReplicationProviderInside other(ReplicationProviderInside peer) {
		return peer == _providerA ? _providerB : _providerA;
	}

	private void putCounterpartRef(Object obj, ReplicationReference otherRef) {
		if (_counterpartRefsByOriginal.get(obj) == null)
			_counterpartRefsByOriginal.put(obj, otherRef);
		else
			throw new RuntimeException("should be unreachable");
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

	private void resetCounterpartRefsByOriginal(){
		_counterpartRefsByOriginal = new Hashtable4(SIZE);
	}

	private void resetProcessedObjectsInThisReplicateCall(){
		_processedObjectsWithinReplicate = new Hashtable4(SIZE);
	}

	private void resetProcessedUuids(){
		_processedUuidsWithinSession = new Hashtable4(SIZE);
	}

	private void storeChangedCounterpartInDestination(ReplicationReference reference, ReplicationProviderInside destination) {
		if (!reference.isMarkedForReplicating()) return;
		destination.storeReplica(reference.counterpart());
	}

	private void storeChangedObjectsIn(final ReplicationProviderInside destination) {
		final ReplicationProviderInside source = other(destination);
		if (_directionTo == source) return;

		source.visitCachedReferences(new Visitor4() {
			public void visit(Object obj) {
				storeChangedCounterpartInDestination((ReplicationReference) obj, destination);
			}
		});
	}

	private void throwReplicationConflictException() {
		throw new ReplicationConflictException("A replication conflict ocurred and the ReplicationEventListener, if any, did not choose which state should override the other.");
	}

	private boolean wasProcessed(Db4oUUID uuid) {
		return _processedUuidsWithinSession.get(uuid) != null;
	}

	private final class ReplicationVisitor implements Visitor {
		public final boolean visit(Object object) {
			if (object instanceof TraversedField) {
				final TraversedField traversedField = ((TraversedField) object);
				return activateObjectToBeReplicated(traversedField.getValue(), traversedField.getReferencingObject(), traversedField.getName());
			} else {
				return activateObjectToBeReplicated(object, null, null);
			}
		}
	}
}
