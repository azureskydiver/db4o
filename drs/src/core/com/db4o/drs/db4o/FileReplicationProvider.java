/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

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
package com.db4o.drs.db4o;

import java.util.*;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ReplicationRecord;
import com.db4o.config.Configuration;
import com.db4o.drs.inside.*;
import com.db4o.ext.Db4oDatabase;
import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.ext.ObjectInfo;
import com.db4o.ext.VirtualField;
import com.db4o.foundation.Tree;
import com.db4o.foundation.Visitor4;
import com.db4o.internal.ExternalObjectContainer;
import com.db4o.internal.Transaction;
import com.db4o.internal.TreeInt;
import com.db4o.internal.replication.Db4oReplicationReference;
import com.db4o.query.Query;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;
import com.db4o.types.*;

// TODO: Add additional query methods (whereModified )

class FileReplicationProvider implements Db4oReplicationProvider {

	private ReadonlyReplicationProviderSignature _mySignature;

	protected final ExternalObjectContainer _stream;

	private final Reflector _reflector;

	private ReplicationRecord _replicationRecord;

	Db4oReplicationReferenceImpl _referencesByObject;

	private Db4oSignatureMap _signatureMap;

	private Tree _idsReplicatedInThisSession;

	private final String _name;

	public FileReplicationProvider(ObjectContainer objectContainer) {
		this(objectContainer, "null");
	}

	public FileReplicationProvider(ObjectContainer objectContainer, String name) {
		Configuration cfg = objectContainer.ext().configure();
		cfg.objectClass(Object.class).cascadeOnDelete(false);
		cfg.callbacks(false);

		_name = name;
		_stream = (ExternalObjectContainer) objectContainer;
		_reflector = _stream.reflector();
		_signatureMap = new Db4oSignatureMap(_stream);
	}

	public ReadonlyReplicationProviderSignature getSignature() {
		if (_mySignature == null) {
			_mySignature = new Db4oReplicationProviderSignature(_stream
					.identity());
		}
		return _mySignature;
	}

	public Object getMonitor() {
		return _stream.lock();
	}

	public void startReplicationTransaction(
			ReadonlyReplicationProviderSignature peerSignature) {

		clearAllReferences();

		synchronized (getMonitor()) {

			Transaction trans = _stream.transaction();

			Db4oDatabase myIdentity = _stream.identity();
			_signatureMap.put(myIdentity);

			Db4oDatabase otherIdentity = _signatureMap.produce(peerSignature
					.getSignature(), peerSignature.getCreated());

			Db4oDatabase younger = null;
			Db4oDatabase older = null;

			if (myIdentity.isOlderThan(otherIdentity)) {
				younger = otherIdentity;
				older = myIdentity;
			} else {
				younger = myIdentity;
				older = otherIdentity;
			}

			_replicationRecord = ReplicationRecord.queryForReplicationRecord(
					_stream, trans, younger, older);
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
			_stream.storeByNewReplication(this, obj);

			// the ID is an int internally, it can be casted to int.
			final TreeInt node = new TreeInt((int) _stream.getID(obj));

			if (_idsReplicatedInThisSession == null)
				_idsReplicatedInThisSession = node;
			else
				_idsReplicatedInThisSession = _idsReplicatedInThisSession
						.add(node);
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

	public ReplicationReference produceReference(Object obj, Object unused,
			String unused2) {

		if (obj == null) {
			return null;
		}

		if (_referencesByObject != null) {
			Db4oReplicationReferenceImpl existingNode = _referencesByObject
					.find(obj);
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
		if (uuid == null)
			throw new NullPointerException();

		Db4oReplicationReferenceImpl newNode = new Db4oReplicationReferenceImpl(
				objectInfo);

		addReference(newNode);

		return newNode;
	}

	protected void refresh(Object obj) {
		//empty in File Provider
	}

	private void addReference(Db4oReplicationReferenceImpl newNode) {
		if (_referencesByObject == null) {
			_referencesByObject = newNode;
		} else {
			_referencesByObject = _referencesByObject.add(newNode);
		}
	}

	public ReplicationReference referenceNewObject(Object obj,
			ReplicationReference counterpartReference,
			ReplicationReference referencingObjCounterPartRef, String fieldName) {

		Db4oUUID uuid = counterpartReference.uuid();

		if (uuid == null) {
			return null;
		}

		byte[] signature = uuid.getSignaturePart();
		long longPart = uuid.getLongPart();
		long version = counterpartReference.version();

		Db4oDatabase db = _signatureMap.produce(signature, 0);

		Db4oReplicationReferenceImpl ref = new Db4oReplicationReferenceImpl(
				obj, db, longPart, version);

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
		if (!_stream.isActive(obj)) {
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
	 * adds a constraint to the passed Query to query only for objects that were
	 * modified since the last replication process between this and the other
	 * ObjectContainer involved in the current replication process.
	 * 
	 * @param query
	 *            the Query to be constrained
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
		_stream.store(o);
	}

	public void update(Object o) {
		_stream.store(o);
	}

	public String getName() {
		return _name;
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
		while (objectSet.hasNext())
			delete(objectSet.next());
	}

	public void delete(Object obj) {
		_stream.delete(obj);
	}

	public boolean wasModifiedSinceLastReplication(
			ReplicationReference reference) {
		if (_idsReplicatedInThisSession != null) {
			int id = (int) _stream.getID(reference.object());
			if (_idsReplicatedInThisSession.find(new TreeInt(id)) != null)
				return false;
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
		if (obj == null)
			return;

		_stream.delete(obj);
	}

	public ExtObjectContainer getObjectContainer() {
		return _stream;
	}

	public boolean isProviderSpecific(Object original) {
		return original instanceof Db4oCollection;
	}

	public void replicationReflector(ReplicationReflector replicationReflector) {
	}
}
