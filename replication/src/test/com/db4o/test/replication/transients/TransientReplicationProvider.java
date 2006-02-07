package com.db4o.test.replication.transients;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Collection4;
import com.db4o.foundation.Iterator4;
import com.db4o.foundation.ObjectSetIterator4Facade;
import com.db4o.foundation.Queue4;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.replication.CollectionHandler;
import com.db4o.inside.replication.CollectionHandlerImpl;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.ReplicationReferenceImpl;
import com.db4o.inside.replication.ReplicationReflector;
import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.inside.traversal.CollectionFlattener;
import com.db4o.inside.traversal.Traverser;
import com.db4o.inside.traversal.Traverser.Visitor;
import com.db4o.reflect.ReflectArray;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.Reflector;
import com.db4o.replication.hibernate.MySignature;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

public class TransientReplicationProvider implements TestableReplicationProvider, TestableReplicationProviderInside {

	private long nextObjectId = 1;

	private final Traverser _traverser;
	private final CollectionHandler _collectionHandler;

	private final Map _storedObjects = new IdentityHashMap();
	private final Map _activatedObjects = new IdentityHashMap();

	private final Map _referencesByObject = new IdentityHashMap();

	private final ReadonlyReplicationProviderSignature _signature;
	private ReadonlyReplicationProviderSignature _peerSignature;

	private long _lastReplicationVersion;
	private final String _name;


	public TransientReplicationProvider(byte[] signature, String name) {
		_signature = new MySignature(signature);
		_name = name;

		ReplicationReflector reflector = new ReplicationReflector();
		_collectionHandler = new CollectionHandlerImpl(reflector.reflector());
		_traverser = new MyTraverser(reflector.reflector(), _collectionHandler);
	}

	public TransientReplicationProvider(byte[] signature) {
		this(signature, null);
	}


	public ReadonlyReplicationProviderSignature getSignature() {
		return _signature;
	}

	public Object getMonitor() {
		return this;
	}

	public void startReplicationTransaction(ReadonlyReplicationProviderSignature peerSignature) {
		if (_peerSignature != null)
			if (! _peerSignature.equals(peerSignature)) {
				throw new IllegalArgumentException("This provider can only replicate with a single peer.");
			}
		_peerSignature = peerSignature;
	}

	public void storeReplicationRecord(long version) {
		_lastReplicationVersion = version;
	}

	public void commit(long raisedDatabaseVersion) {
		// do nothing
	}

	public void rollbackReplication() {
		throw new UnsupportedOperationException();
	}

	public long getCurrentVersion() {
		return _lastReplicationVersion + 1;
	}

	public ObjectSet objectsChangedSinceLastReplication() {
		return objectsChangedSinceLastReplication(Object.class);
	}

	public ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		Collection4 result = new Collection4();
		for (ObjectSet iterator = getStoredObjects(clazz); iterator.hasNext();) {
			Object candidate = iterator.next();
			if (wasChangedSinceLastReplication(candidate))
				result.add(candidate);
		}
		return new ObjectSetIterator4Facade(result.iterator());
	}

	private boolean wasChangedSinceLastReplication(Object candidate) {
		return getInfo(candidate)._version > _lastReplicationVersion;
	}

	private ObjectInfo getInfo(Object candidate) {
		return (ObjectInfo) _storedObjects.get(candidate);
	}

	public long getLastReplicationVersion() {
		return _lastReplicationVersion;
	}

	private void store(Object obj, Db4oUUID uuid, long version) {
		if (obj == null) throw new RuntimeException();
		_storedObjects.put(obj, new ObjectInfo(uuid, version));
	}

	public void storeReplica(Object obj) {
		ReplicationReference ref = getCachedReference(obj);
		if (ref == null) {
			throw new RuntimeException();
		}
		store(obj, ref.uuid(), ref.version());
	}

	public ReplicationReference produceReference(Object obj) {
		ReplicationReference cached = getCachedReference(obj);
		if (cached != null) return cached;

		if (!isStored(obj)) return null;

		return createReferenceFor(obj);
	}

	public ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference, Object referencingObj, String fieldName) {
		Db4oUUID uuid = counterpartReference.uuid();
		long version = counterpartReference.version();

		return createReference(obj, uuid, version);
	}

	private ReplicationReference createReference(Object obj, Db4oUUID uuid, long version) {
		ReplicationReference result = new ReplicationReferenceImpl(obj, uuid, version);
		_referencesByObject.put(obj, result);
		return result;
	}

	private boolean isStored(Object obj) {
		return getInfo(obj) != null;
	}

	private ReplicationReference getCachedReference(Object obj) {
		return (ReplicationReference) _referencesByObject.get(obj);
	}

	private ReplicationReference createReferenceFor(Object obj) {
		MyReplicationReference result = new MyReplicationReference(obj);
		_referencesByObject.put(obj, result);
		return result;
	}

	public ReplicationReference produceReferenceByUUID(Db4oUUID uuid, Class hintIgnored) {
		if (uuid == null) {
			return null;
		}
		Object object = getObjectByUUID(uuid);
		if (object == null) return null;
		return produceReference(object);
	}

	private Object getObjectByUUID(Db4oUUID uuid) {
		ObjectSet iter = getStoredObjects();
		while (iter.hasNext()) {
			Object candidate = iter.next();
			if (getInfo(candidate)._uuid.equals(uuid)) return candidate;
		}
		return null;
	}

	public ObjectSet getStoredObjects() {
		return getStoredObjects(Object.class);
	}

	public void setSignature(byte[] b) {
	}

	public ObjectSet getStoredObjects(Class clazz) {
		Collection4 result = new Collection4();
		for (Iterator iterator = _storedObjects.keySet().iterator(); iterator.hasNext();) {
			Object candidate = iterator.next();
			if (clazz.isAssignableFrom(candidate.getClass()))
				result.add(candidate);
		}
		return new ObjectSetIterator4Facade(result.iterator());
	}

	public void storeNew(Object o) {
		_traverser.traverseGraph(o, new Visitor() {
			public boolean visit(Object obj) {
				if (_storedObjects.get(obj) != null) {
					return false;
				}
				transientProviderSpecificStore(obj);
				return true;
			}
		});
	}

	public void update(Object o) {
		transientProviderSpecificStore(o);
	}

	public String getName() {
		return _name;
	}

	public void transientProviderSpecificStore(Object obj) {
		ObjectInfo info = getInfo(obj);
		if (info == null)
			store(obj, new Db4oUUID(nextObjectId++, _signature.getBytes()), getCurrentVersion());
		else
			info._version = getCurrentVersion();
	}

	public boolean hasReplicationReferenceAlready(Object obj) {
		return getCachedReference(obj) != null;
	}

	public void visitCachedReferences(Visitor4 visitor) {
		Iterator i = _referencesByObject.values().iterator();
		while (i.hasNext()) {
			visitor.visit(i.next());
		}
	}

	public void clearAllReferences() {
		_referencesByObject.clear();
	}

	public void activate(Object object) {
		_activatedObjects.put(object, object);
	}

	public Map activatedObjects() {
		return _activatedObjects;
	}

	private class MyReplicationReference implements ReplicationReference {

		private final Object _object;
		private Object _counterpart;
		private boolean _isMarkedForReplicating;

		MyReplicationReference(Object object) {
			if (object == null) throw new IllegalArgumentException();
			_object = object;
		}

		public Object object() {
			return _object;
		}

		public Object counterpart() {
			return _counterpart;
		}

		public long version() {
			return getInfo(_object)._version;
		}

		public Db4oUUID uuid() {
			return getInfo(_object)._uuid;
		}

		public void setCounterpart(Object obj) {
			_counterpart = obj;
		}

		public void markForReplicating() {
			_isMarkedForReplicating = true;
		}

		public boolean isMarkedForReplicating() {
			return _isMarkedForReplicating;
		}

	}


	private static class ObjectInfo {

		private final Db4oUUID _uuid;
		private long _version;

		public ObjectInfo(Db4oUUID uuid, long version) {
			_uuid = uuid;
			_version = version;
		}

	}

	public String toString() {
		return _name;
	}

	public void closeIfOpened() {
		// do nothing
	}

	public void commit() {
		// do nothing
	}

	public void delete(Class clazz) {
		ObjectSet iterator = getStoredObjects(clazz);
		while (iterator.hasNext()) {
			_storedObjects.remove(iterator.next());
		}
	}

	//Copied from com.db4o.inside.traversal.TraverserImpl v1.2 because v1.3 contains Replication-specific code
	public class MyTraverser implements Traverser {

		private final Reflector _reflector;
		private final ReflectArray _arrayReflector;
		private final CollectionFlattener _collectionFlattener;
		private final Queue4 _queue = new Queue4();

		public MyTraverser(Reflector reflector, CollectionFlattener collectionFlattener) {
			_reflector = reflector;
			_arrayReflector = _reflector.array();
			_collectionFlattener = collectionFlattener;
		}

		public void traverseGraph(Object object, Visitor visitor) {
			queueUpForTraversing(object);
			while (true) {
				Object next = _queue.next();
				if (next == null) return;
				traverseObject(next, visitor);
			}
		}

		private void traverseObject(Object object, Visitor visitor) {
			if (!visitor.visit(object)) {
				return;
			}

			ReflectClass claxx = _reflector.forObject(object);
			traverseFields(object, claxx);
		}

		private void traverseFields(Object object, ReflectClass claxx) {
			ReflectField[] fields;

			fields = claxx.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				ReflectField field = fields[i];
				if (field.isStatic()) continue;
				if (field.isTransient()) continue;
				field.setAccessible(); //TODO Optimize: Change the reflector so I dont have to call setAcessible all the time.

				Object value = field.get(object);
				queueUpForTraversing(value);
			}

			ReflectClass superclass = claxx.getSuperclass();
			if (superclass == null) return;
			traverseFields(object, superclass);
		}

		private void traverseCollection(Object collection) {
			Iterator4 elements = _collectionFlattener.iteratorFor(collection); //TODO Optimization: visit instead of flattening.
			while (elements.hasNext()) {
				Object element = elements.next();
				if (element == null) continue;
				queueUpForTraversing(element);
			}
		}

		private void traverseArray(Object array, ReflectClass arrayClass) {
			Object[] contents = contents(array);
			for (int i = 0; i < contents.length; i++) {
				queueUpForTraversing(contents[i]);
			}
		}

		private void queueUpForTraversing(Object object) {
			if (object == null) return;
			ReflectClass claxx = _reflector.forObject(object);
			if (isSecondClass(claxx)) return;

			if (_collectionFlattener.canHandle(claxx)) {
				traverseCollection(object);
				_queue.add(object);
			} else {
				if (claxx.isArray()) {
					traverseArray(object, claxx);
				} else {
					_queue.add(object);
				}
			}
		}

		private boolean isSecondClass(ReflectClass claxx) {
			//      TODO Optimization: Compute this lazily in ReflectClass;
			if (claxx.isSecondClass()) return true;
			return claxx.isArray() && claxx.getComponentType().isSecondClass();
		}


		final Object[] contents(Object array) { //FIXME Eliminate duplication. Move this to ReflectArray. This logic is in the GenericReplicationSessio too.
			int[] dim = _arrayReflector.dimensions(array);
			Object[] result = new Object[volume(dim)];
			_arrayReflector.flatten(array, dim, 0, result, 0); //TODO Optimize add a visit(Visitor) method to ReflectArray or navigate the array to avoid copying all this stuff all the time.
			return result;
		}

		private int volume(int[] dim) { //FIXME Eliminate duplication. Move this to ReflectArray. This logic is in the GenericReplicationSessio too.
			int result = dim[0];
			for (int i = 1; i < dim.length; i++) {
				result = result * dim[i];
			}
			return result;
		}

		public void extendTraversalTo(Object disconnected) {
			queueUpForTraversing(disconnected);
		}
	}
}
