/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs.db4o;

import java.util.Iterator;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ReplicationRecord;
import com.db4o.cs.YapClient;
import com.db4o.config.Configuration;
import com.db4o.drs.inside.ReadonlyReplicationProviderSignature;
import com.db4o.drs.inside.ReplicationReference;
import com.db4o.drs.inside.TestableReplicationProvider;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.ObjectInfo;
import com.db4o.ext.VirtualField;
import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.replication.Db4oReplicationReference;
import com.db4o.inside.replication.Db4oReplicationReferenceProvider;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;

//TODO: Add additional query methods (whereModified )


public class Db4oReplicationProvider implements TestableReplicationProvider, Db4oReplicationReferenceProvider, TestableReplicationProviderInside {

	private ReadonlyReplicationProviderSignature _mySignature;

	private final YapStream _stream;

	private final Reflector _reflector;

	private ReplicationRecord _replicationRecord;

	private Db4oReplicationReferenceImpl _referencesByObject;

	private Db4oSignatureMap _signatureMap;

	private Tree _idsReplicatedInThisSession;

	private final String _name;

	public Db4oReplicationProvider(ObjectContainer objectContainer) {
		this(objectContainer, "null");
	}

	public Db4oReplicationProvider(ObjectContainer objectContainer, String name) {
		Configuration cfg = objectContainer.ext().configure();
		cfg.objectClass(Object.class).cascadeOnDelete(false);
		cfg.callbacks(false);

		_name = name;
		_stream = (YapStream) objectContainer;
		_reflector = _stream.reflector();
		_signatureMap = new Db4oSignatureMap(_stream);
	}

	public ObjectContainer objectContainer() {
		return _stream;
	}
	
	public ReadonlyReplicationProviderSignature getSignature() {
		if (_mySignature == null) {
			_mySignature = new Db4oReplicationProviderSignature(_stream.identity());
		}
		return _mySignature;
	}

	public Object getMonitor() {
		return _stream.lock();
	}

	public void startReplicationTransaction(ReadonlyReplicationProviderSignature peerSignature) {

		clearAllReferences();

		synchronized (getMonitor()) {

			Transaction trans = _stream.getTransaction();

			Db4oDatabase myIdentity = _stream.identity();
			_signatureMap.put(myIdentity);

			Db4oDatabase otherIdentity = _signatureMap.produce(peerSignature.getSignature(), peerSignature.getCreated());

			Db4oDatabase younger = null;
			Db4oDatabase older = null;

			if (myIdentity.isOlderThan(otherIdentity)) {
				younger = otherIdentity;
				older = myIdentity;
			} else {
				younger = myIdentity;
				older = otherIdentity;
			}

			_replicationRecord = ReplicationRecord.queryForReplicationRecord(_stream, younger, older);
			if (_replicationRecord == null) {
				_replicationRecord = new ReplicationRecord(younger, older);
				_replicationRecord.store(_stream);
			}

			long localInitialVersion = _stream.version();
		}
	}

	public void syncVersionWithPeer(long version) {
		long versionTest = getCurrentVersion();
		_replicationRecord._version = version;
		_replicationRecord.store(_stream);
	}

	public void commitReplicationTransaction(long raisedDatabaseVersion) {

		long versionTest = getCurrentVersion();

		_stream.raiseVersion(raisedDatabaseVersion);
		_stream.commit();
		_idsReplicatedInThisSession = null;

	}

	public void rollbackReplication() {
		_stream.rollback();
		_referencesByObject = null;
		_idsReplicatedInThisSession = null;
	}

	public long getCurrentVersion() {
		return _stream.version();
	}

	public long getLastReplicationVersion() {
		return _replicationRecord._version;
	}

	public void storeReplica(Object obj) {
		synchronized (getMonitor()) {
			_stream.setByNewReplication(this, obj);

			//the ID is an int internally, it can be casted to int.
			final TreeInt node = new TreeInt((int) _stream.getID(obj));

			if (_idsReplicatedInThisSession == null)
				_idsReplicatedInThisSession = node;
			else
				_idsReplicatedInThisSession = _idsReplicatedInThisSession.add(node);
		}
	}

	public void activate(Object obj) {

		if (obj == null) {
			return;
		}

		ReflectClass claxx = _reflector.forObject(obj);

		int level = claxx.isCollection() ? 3 : 1;

		_stream.activate(obj, level);

	}

	public Db4oReplicationReference referenceFor(Object obj) {
		if (_referencesByObject == null) {
			return null;
		}
		return _referencesByObject.find(obj);
	}

	public ReplicationReference produceReference(Object obj, Object unused, String unused2) {

		if (obj == null) {
			return null;
		}

		if (_referencesByObject != null) {
			Db4oReplicationReferenceImpl existingNode = _referencesByObject.find(obj);
			if (existingNode != null) {
				return existingNode;
			}
		}

		refresh(obj);

		ObjectInfo objectInfo = _stream.getObjectInfo(obj);

		if (objectInfo == null) {
			return null;
		}

		Db4oUUID uuid = objectInfo.getUUID();
			if (uuid==null) throw new NullPointerException();

		Db4oReplicationReferenceImpl newNode = new Db4oReplicationReferenceImpl(objectInfo);

		addReference(newNode);

		return newNode;
	}

	private void refresh(Object obj) {
		//TODO FIXME, fix for C/S, not required in SOLO
		if (_stream instanceof YapClient)
			_stream.refresh(obj, 1);
	}

	private void addReference(Db4oReplicationReferenceImpl newNode) {
		if (_referencesByObject == null) {
			_referencesByObject = newNode;
		} else {
			_referencesByObject = _referencesByObject.add(newNode);
		}
	}

	public ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference, ReplicationReference referencingObjCounterPartRef, String fieldName) {

		Db4oUUID uuid = counterpartReference.uuid();

		if (uuid == null) {
			return null;
		}

		byte[] signature = uuid.getSignaturePart();
		long longPart = uuid.getLongPart();
		long version = counterpartReference.version();

		Db4oDatabase db = _signatureMap.produce(signature, 0);

		Db4oReplicationReferenceImpl ref = new Db4oReplicationReferenceImpl(obj, db, longPart, version);

		addReference(ref);

		return ref;
	}

	public ReplicationReference produceReferenceByUUID(Db4oUUID uuid, Class hint) {
		if (uuid == null) {
			return null;
		}
		Object obj = _stream.getByUUID(uuid);
		if (obj == null) {
			return null;
		}
		if (! _stream.isActive(obj)) {
			_stream.activate(obj, 1);
		}
		return produceReference(obj, null, null);
	}

	public void visitCachedReferences(final Visitor4 visitor) {
		if (_referencesByObject != null) {
			_referencesByObject.traverse(new Visitor4() {
				public void visit(Object obj) {
					Db4oReplicationReferenceImpl node = (Db4oReplicationReferenceImpl) obj;
					visitor.visit(node);
				}
			});
		}
	}

	public void clearAllReferences() {
		_referencesByObject = null;
	}

	public ObjectSet objectsChangedSinceLastReplication() {
		Query q = _stream.query();
		whereModified(q);
		return q.execute();
	}

	public ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		Query q = _stream.query();
		q.constrain(clazz);
		whereModified(q);
		return q.execute();
	}

	/**
	 * adds a constraint to the passed Query to query only for objects that
	 * were modified since the last replication process between this and the
	 * other ObjectContainer involved in the current replication process.
	 *
	 * @param query the Query to be constrained
	 */
	public void whereModified(Query query) {
		query.descend(VirtualField.VERSION).constrain(
				new Long(getLastReplicationVersion())).greater();
	}

	public ObjectSet getStoredObjects(Class type) {
		Query query = _stream.query();
		query.constrain(type);
		return query.execute();
	}

	public void storeNew(Object o) {
		_stream.set(o);
	}

	public void update(Object o) {
		_stream.set(o);
	}

	public String getName() {
		return _name;
	}

	public void updateCounterpart(Object updated) {
		throw new RuntimeException("TODO");
	}

	public void destroy() {
		// do nothing
	}

	public void commit() {
		_stream.commit();
	}

	public void deleteAllInstances(Class clazz) {
		Query q = _stream.query();
		q.constrain(clazz);
		Iterator objectSet = q.execute().iterator();
		while (objectSet.hasNext()) delete(objectSet.next());
	}

	public void delete(Object obj) {
		_stream.delete(obj);
	}

	public boolean wasModifiedSinceLastReplication(ReplicationReference reference) {
		if (_idsReplicatedInThisSession != null) {
			int id = (int) _stream.getID(reference.object());
			if (_idsReplicatedInThisSession.find(new TreeInt(id)) != null) return false;
		}

		return reference.version() > getLastReplicationVersion();
	}

	public boolean supportsMultiDimensionalArrays() {
		return true;
	}

	public boolean supportsHybridCollection() {
		return true;
	}

	public boolean supportsRollback() {
		return false;
	}

	public boolean supportsCascadeDelete() {
		return true;
	}

	public String toString() {
		return getName();
	}

	public void replicateDeletion(Db4oUUID uuid) {
		Object obj = _stream.getByUUID(uuid);
		if (obj == null) return;

		_stream.delete(obj);
	}

	public ExtObjectContainer getObjectContainer() {
		return _stream;
	}
}
