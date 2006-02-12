package com.db4o.inside.replication;

import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.traversal.Field;
import com.db4o.inside.traversal.Traverser;
import com.db4o.inside.traversal.Traverser.Visitor;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.replication.ConflictResolver;
import com.db4o.replication.ReplicationProvider;
import com.db4o.replication.ReplicationSession;

public class GenericReplicationSession implements ReplicationSession {


	private final ReplicationReflector _reflector;

	private final CollectionHandler _collectionHandler;

	private final ReplicationProviderInside _peerA;
	private final ReplicationProviderInside _peerB;

	private ReplicationProvider _directionTo; //null means bidirectional replication.

	private final ConflictResolver _resolver;

	private final long _lastReplicationVersion;
	private final Traverser _traverser;

	/**
	 * key = object originated from one provider
	 * value = the counterpart ReplicationReference of the original object
	 */
	private final Hashtable4 _originalCounterPartRefMap = new Hashtable4(10000);

	public GenericReplicationSession(ReplicationProvider providerA, ReplicationProvider providerB, ConflictResolver resolver) {

		_reflector = new ReplicationReflector();
		_collectionHandler = new CollectionHandlerImpl(_reflector.reflector());
		_traverser = new ReplicationTraverser(_reflector.reflector(), _collectionHandler);

		_peerA = (ReplicationProviderInside) providerA;
		_peerB = (ReplicationProviderInside) providerB;
		_resolver = resolver;

		synchronized (_peerA.getMonitor()) {
			synchronized (_peerB.getMonitor()) {
				_peerA.startReplicationTransaction(_peerB.getSignature());
				_peerB.startReplicationTransaction(_peerA.getSignature());
			}
		}

		_lastReplicationVersion = _peerA.getLastReplicationVersion();
	}

	public GenericReplicationSession(ReplicationProviderInside _peerA, ReplicationProviderInside _peerB) {
		this(_peerA, _peerB, new DefaultConflictResolver());
	}

	public void replicate(Object root) {
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

	private void activateGraphToBeReplicated(Object root) {
		_traverser.traverseGraph(root, new Visitor() {
			public boolean visit(Object object) {
				if (object instanceof Field) {
					final Field field = ((Field) object);
					return activateObjectToBeReplicated(field.getValue(), field.getReferencingObject(), field.getName());
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
		source.visitCachedReferences(new Visitor4() {
			public void visit(Object obj) {
				storeChangedCounterpartInDestination((ReplicationReference) obj, destination);
			}
		});
	}

	private void storeChangedCounterpartInDestination(ReplicationReference reference, ReplicationProviderInside destination) {
		if (!reference.isMarkedForReplicating()) return;
		destination.storeReplica(reference.counterpart());
	}

	ReplicationReference getReferencingObjectCounterPartRef(Object referencingObject) {
		return (ReplicationReference) _originalCounterPartRefMap.get(referencingObject);
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

		if (refA == null)
			refA = otherRef;
		else
			refB = otherRef;

		if (otherRef == null) {  //New object to the other peer.
			if (_directionTo == owner) return false;

			owner.activate(obj);

			Object counterpart = emptyClone(owner, obj);

			ownerRef.setCounterpart(counterpart);
			ownerRef.markForReplicating();

			otherRef = other.referenceNewObject(counterpart, ownerRef, getReferencingObjectCounterPartRef(referencingObject), fieldName);
			_originalCounterPartRefMap.put(obj, otherRef);

			// TODO: We might not need counterpart in otherRef. Check.
			if (otherRef != null) {

				otherRef.setCounterpart(obj);

			}

			return true;
		}

		ownerRef.setCounterpart(otherRef.object());

		Object objectA = refA.object();
		Object objectB = refB.object();

		boolean changedInA = _peerA.wasChangedSinceLastReplication(refA);
		boolean changedInB = _peerB.wasChangedSinceLastReplication(refB);

		if (!changedInA && !changedInB) return false;

		boolean conflict = false;
		if (changedInA && changedInB) conflict = true;
		if (changedInA && _directionTo == _peerA) conflict = true;
		if (changedInB && _directionTo == _peerB) conflict = true;

		Object prevailing = obj;
		if (conflict) {
			_peerA.activate(objectA);
			_peerB.activate(objectB);
			prevailing = _resolver.resolveConflict(this, objectA, objectB);
			if (prevailing == null) return false;
			if (prevailing != objectA && prevailing != objectB)
				throw new RuntimeException("ConflictResolver must return objectA, objectB or null."); //FIXME Use Db4o's standard exception throwing mechanism.
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


	private Object emptyClone(ReplicationProviderInside sourceProvider, Object obj) {
		if (obj == null) return null;
		ReflectClass claxx = _reflector.forObject(obj);
		if (claxx.isSecondClass()) return obj;
		if (claxx.isArray()) return arrayClone(obj, claxx, sourceProvider);
		if (_collectionHandler.canHandle(claxx)) {
			return collectionClone(obj, claxx, sourceProvider);
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

		//TODO supports collection here
		Object result = sourceProvider.produceReference(value, null, null).counterpart();
		if (result == null)
			throw new RuntimeException();
		return result;
	}

	private Object collectionClone(Object original, ReflectClass claxx, final ReplicationProviderInside sourceProvider) {
		final GenericReplicationSession grs = this;
		return _collectionHandler.cloneWithCounterparts(original, claxx, new CounterpartFinder() { //TODO Optimize: Have a single CounterpartFinder instance. Dont create it all the time.

			public Object findCounterpart(Object original) {
				return grs.findCounterpart(original, sourceProvider);
			}
		});
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

			//TODO supports collection here
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
		_peerA.closeIfOpened();
		_peerB.closeIfOpened();
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
