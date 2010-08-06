/* Copyright (C) 2004 - 2008  Versant Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.test;

import java.util.*;

import com.db4o.*;
import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.inside.traversal.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

public class TransientReplicationProvider implements TestableReplicationProvider, TestableReplicationProviderInside {
	
	private TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();

	private final String _name;

	private Traverser _traverser;

	private final Map _storedObjects = new IdentityHashMap();

	private final Map _activatedObjects = new IdentityHashMap();

	private final Map _referencesByObject = new IdentityHashMap();

	private final MySignature _signature;

	private ReadonlyReplicationProviderSignature _peerSignature;

	private long _lastReplicationVersion = 0;

	private Collection4 _uuidsDeletedSinceLastReplication = new Collection4();

	public TransientReplicationProvider(byte[] signature, String name) {
		_signature = new MySignature(signature);
		_name = name;
	}

	public String toString() {
		return _name;
	}

// --------------------- Interface ReplicationProvider ---------------------

	public ObjectSet objectsChangedSinceLastReplication() {
		return objectsChangedSinceLastReplication(Object.class);
	}

	public ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		Collection4 result = new Collection4();
		for (Iterator4 iterator = storedObjectsCollection(clazz).iterator(); iterator.moveNext();) {
			Object candidate = iterator.current();
			if (wasChangedSinceLastReplication(candidate))
				result.add(candidate);
		}
		return new ObjectSetCollection4Facade(result);
	}

// --------------------- Interface ReplicationProviderInside ---------------------

	public void activate(Object object) {
		_activatedObjects.put(object, object);
	}

	public void clearAllReferences() {
		_referencesByObject.clear();
	}

	public void commitReplicationTransaction(long raisedDatabaseVersion) {
		_uuidsDeletedSinceLastReplication.clear();
		_timeStampIdGenerator.setMinimumNext(raisedDatabaseVersion);
	}

	public void destroy() {
		// do nothing
	}

	public long getCurrentVersion() {
		return _timeStampIdGenerator.generate();
	}

	public long getLastReplicationVersion() {
		return _lastReplicationVersion;
	}

	public String getName() {
		return _name;
	}

	public ReadonlyReplicationProviderSignature getSignature() {
		return _signature;
	}

	public ReplicationReference produceReference(Object obj, Object unused, String unused2) {
		ReplicationReference cached = getCachedReference(obj);
		if (cached != null) return cached;

		if (!isStored(obj)) return null;

		return createReferenceFor(obj);
	}

	public ReplicationReference produceReferenceByUUID(DrsUUID uuid, Class hintIgnored) {
		if (uuid == null) {
			return null;
		}
		Object object = getObject(uuid);
		if (object == null) return null;
		return produceReference(object, null, null);
	}

	public ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference, ReplicationReference unused, String unused2) {
		//System.out.println("referenceNewObject: " + obj + "  UUID: " + counterpartReference.uuid());
		DrsUUID uuid = counterpartReference.uuid();
		long version = counterpartReference.version();

		if (getObject(uuid) != null) throw new RuntimeException("Object exists already.");

		ReplicationReference result = createReferenceFor(obj);
		store(obj, uuid, version);
		return result;
	}

	public void rollbackReplication() {
		throw new UnsupportedOperationException();
	}

	public void startReplicationTransaction(ReadonlyReplicationProviderSignature peerSignature) {
		if (_peerSignature != null)
			if (! _peerSignature.equals(peerSignature))
				throw new IllegalArgumentException("This provider can only replicate with a single peer.");

		_peerSignature = peerSignature;

		_timeStampIdGenerator.setMinimumNext(_lastReplicationVersion);
	}

	public void storeReplica(Object obj) {
		ReplicationReference ref = getCachedReference(obj);
		if (ref == null) {
			throw new RuntimeException();
		}
		store(obj, ref.uuid(), ref.version());
	}

	public void syncVersionWithPeer(long version) {
		_lastReplicationVersion = version;
	}

	public void updateCounterpart(Object obj) {
		storeReplica(obj);
	}

	public void visitCachedReferences(Visitor4 visitor) {
		Iterator i = _referencesByObject.values().iterator();
		while (i.hasNext()) {
			visitor.visit(i.next());
		}
	}

	public boolean wasModifiedSinceLastReplication(ReplicationReference reference) {
		return reference.version() > _lastReplicationVersion;
	}

// --------------------- Interface SimpleObjectContainer ---------------------

	public void commit() {
		// do nothing
	}

	public void delete(Object obj) {
		DrsUUID uuid = produceReference(obj, null, null).uuid();
		_uuidsDeletedSinceLastReplication.add(uuid);
		_storedObjects.remove(obj);
	}

	public void deleteAllInstances(Class clazz) {
		Iterator4 iterator = storedObjectsCollection(clazz).iterator();
		while (iterator.moveNext()) delete(iterator.current());
	}

	public ObjectSet getStoredObjects(Class clazz) {
		return new ObjectSetCollection4Facade(storedObjectsCollection(clazz));
	}

	private Collection4 storedObjectsCollection(Class clazz) {
		Collection4 result = new Collection4();
		for (Iterator iterator = _storedObjects.keySet().iterator(); iterator.hasNext();) {
			Object candidate = iterator.next();
			if (clazz.isAssignableFrom(candidate.getClass()))
				result.add(candidate);
		}
		return result;
	}

	public void storeNew(Object o) {
		_traverser.traverseGraph(o, new Visitor() {
			public boolean visit(Object obj) {
				if (isStored(obj)) return false;
				transientProviderSpecificStore(obj);
				return true;
			}
		});
	}

	public void update(Object o) {
		transientProviderSpecificStore(o);
	}

// --------------------- Interface TestableReplicationProviderInside ---------------------

	public boolean supportsHybridCollection() {
		return true;
	}

	public boolean supportsMultiDimensionalArrays() {
		return true;
	}

	public boolean supportsRollback() {
		return false;
	}

	public Map activatedObjects() {
		return _activatedObjects;
	}

	private ReplicationReference createReferenceFor(Object obj) {
		MyReplicationReference result = new MyReplicationReference(obj);
		_referencesByObject.put(obj, result);
		return result;
	}

	private ReplicationReference getCachedReference(Object obj) {
		return (ReplicationReference) _referencesByObject.get(obj);
	}

	private ObjectInfo getInfo(Object candidate) {
		return (ObjectInfo) _storedObjects.get(candidate);
	}

	public Object getObject(DrsUUID uuid) {
		Iterator4 iter = storedObjectsCollection(Object.class).iterator();
		while (iter.moveNext()) {
			Object candidate = iter.current();
			if (getInfo(candidate)._uuid.equals(uuid)) return candidate;
		}
		return null;
	}

	public ObjectSet getStoredObjects() {
		return getStoredObjects(Object.class);
	}

	private boolean isStored(Object obj) {
		return getInfo(obj) != null;
	}

	public void replicateDeletion(ReplicationReference reference) {
		_storedObjects.remove(reference.object());
	}

	private void store(Object obj, DrsUUID uuid, long version) {
		if (obj == null) throw new RuntimeException();
		_storedObjects.put(obj, new ObjectInfo(uuid, version));
	}

	public void transientProviderSpecificStore(Object obj) {
		//TODO ak: this implementation of vvv is copied from Hibernate, which works.
		// However, vvv should be supposed to be replaced by getCurrentVersion(), but that wouldn't work. Find out
		long vvv = new TimeStampIdGenerator(_lastReplicationVersion).generate();

		ObjectInfo info = getInfo(obj);
		if (info == null)
			store(obj, new DrsUUIDImpl(new Db4oUUID(_timeStampIdGenerator.generate(), _signature.getSignature())), vvv);
		else
			info._version = vvv;
	}

	public ObjectSet uuidsDeletedSinceLastReplication() {
		return new ObjectSetCollection4Facade(_uuidsDeletedSinceLastReplication);
	}

	private boolean wasChangedSinceLastReplication(Object candidate) {
		return getInfo(candidate)._version > _lastReplicationVersion;
	}

	public boolean wasDeletedSinceLastReplication(DrsUUID uuid) {
		return _uuidsDeletedSinceLastReplication.contains(uuid);
	}

	public class MySignature implements ReadonlyReplicationProviderSignature {
		private final byte[] _bytes;
		private long creatimeTime;

		public MySignature(byte[] signature) {
			_bytes = signature;
			creatimeTime = System.currentTimeMillis();
		}

		public long getId() {
			throw new RuntimeException("Never used?");
		}

		public byte[] getSignature() {
			return _bytes;
		}

		public long getCreated() {
			return creatimeTime;
		}
	}

	private class MyReplicationReference implements ReplicationReference {
		private final Object _object;
		private Object _counterpart;
		private boolean _isMarkedForReplicating;
		private boolean _isMarkedForDeleting;

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

		public DrsUUID uuid() {
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

		public void markForDeleting() {
			_isMarkedForDeleting = true;
		}

		public boolean isMarkedForDeleting() {
			return _isMarkedForDeleting;
		}

		boolean objectIsNew;

		public void markCounterpartAsNew() {
			objectIsNew = true;
		}

		public boolean isCounterpartNew() {
			return objectIsNew;
		}
	}

	private static class ObjectInfo {
		public final DrsUUID _uuid;
		public long _version;

		public ObjectInfo(DrsUUID uuid, long version) {
			_uuid = uuid;
			_version = version;
		}
	}

	public class MyTraverser implements Traverser {
		Traverser _delegate;

		public MyTraverser(ReplicationReflector reflector, CollectionHandler collectionHandler) {
			_delegate = new GenericTraverser(reflector, collectionHandler);
		}

		public void traverseGraph(Object object, Visitor visitor) {
			_delegate.traverseGraph(object, visitor);
		}

		public void extendTraversalTo(Object disconnected) {
			_delegate.extendTraversalTo(disconnected);
		}
	}

	public void replicateDeletion(DrsUUID uuid) {
		_storedObjects.remove(getObject(uuid));
	}

	public boolean isProviderSpecific(Object original) {
		return false;
	}

	public void replicationReflector(ReplicationReflector replicationReflector) {
		CollectionHandler _collectionHandler = new CollectionHandlerImpl(replicationReflector);
		_traverser = new MyTraverser(replicationReflector, _collectionHandler);
	}
	
	public ReplicationReference produceReference(Object obj) {
		return produceReference(obj, null, null);
	}

	public void runIsolated(Block4 block) {
		synchronized(this) {
			block.run();
		}
	}
}
