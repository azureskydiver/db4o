package com.db4o.replication.hibernate;

import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.CollectionHandlerImpl;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.ReplicationReferenceImpl;
import com.db4o.inside.replication.ReplicationReflector;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.inside.traversal.CollectionFlattener;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.Reflector;
import com.db4o.replication.hibernate.common.ChangedObjectId;
import com.db4o.replication.hibernate.common.Common;
import com.db4o.replication.hibernate.common.MySignature;
import com.db4o.replication.hibernate.common.PeerSignature;
import com.db4o.replication.hibernate.common.ReplicationComponentField;
import com.db4o.replication.hibernate.common.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.common.ReplicationProviderSignature;
import com.db4o.replication.hibernate.common.ReplicationRecord;
import com.db4o.replication.hibernate.common.UuidLongPartGenerator;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.PostUpdateEventListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractReplicationProvider implements
		HibernateReplicationProvider, TestableReplicationProviderInside {

	protected FlushEventListener myFlushEventListener;

	protected PostUpdateEventListener myPostUpdateEventListener;

	/**
	 * Hibernate mapped classes
	 */
	protected Set _mappedClasses;

	/**
	 * Objects which meta data not yet updated.
	 */
	protected final Set _dirtyRefs = new HashSet();

	/**
	 * The ReplicationProviderSignature of this  Hibernate-mapped database.
	 */
	protected MySignature _mySig;

	/**
	 * The Signature of the peer in the current Transaction.
	 */
	protected PeerSignature _peerSignature;

	protected final Map _referencesByObject = new IdentityHashMap();
	/**
	 * The ReplicationRecord of {@link #_peerSignature}.
	 */
	protected ReplicationRecord _replicationRecord;
	/**
	 * Current transaction number = {@link #getLastReplicationVersion()} + 1. The
	 * minimum version number is 1, when this database is never replicated with
	 * other peers.
	 */
	protected long _currentVersion;

	protected String _name;

	protected final CollectionFlattener _collectionHandler = new CollectionHandlerImpl();

	protected final Set _uuidsReplicatedInThisSession = new HashSet();

	protected boolean _inReplication = false;

	protected Reflector _reflector = new ReplicationReflector().reflector();

	protected UuidLongPartGenerator uuidLongPartGenerator;

	protected void initPeerSigAndRecord(byte[] peerSigBytes, Session session) {
		PeerSignature existingPeerSignature = getPeerSignature(peerSigBytes, session);
		if (existingPeerSignature == null) {
			this._peerSignature = new PeerSignature(peerSigBytes);
			session.save(this._peerSignature);
			session.flush();
			if (getPeerSignature(peerSigBytes, session) == null)
				throw new RuntimeException("Cannot insert existingPeerSignature");
			_replicationRecord = new ReplicationRecord();
			_replicationRecord.setPeerSignature(_peerSignature);
		} else {
			this._peerSignature = existingPeerSignature;
			_replicationRecord = getRecord(_peerSignature);
		}
	}

	public final String toString() {
		return "name = " + _name + ", sig = " + flattenBytes(getSignature().getBytes());
	}

	protected void ensureReplicationActive() {
		if (!_inReplication)
			throw new UnsupportedOperationException("Method not supported because replication transaction is not active");
	}

	protected void ensureReplicationInActive() {
		if (_inReplication)
			throw new UnsupportedOperationException("Method not supported because replication transaction is active");
	}

	protected static String flattenBytes(byte[] b) {
		String out = "";
		for (int i = 0; i < b.length; i++) {
			out += ", " + b[i];
		}
		return out;
	}

	public final ReadonlyReplicationProviderSignature getSignature() {
		return _mySig;
	}

	public final Object getMonitor() {
		return this;
	}

	public final long getCurrentVersion() {
		ensureReplicationActive();

		return _currentVersion;
	}

	public final long getLastReplicationVersion() {
		ensureReplicationActive();

		return getCurrentVersion() - 1;
	}

	public final void activate(Object object) {
		ensureReplicationActive();

		Hibernate.initialize(object);
	}

	public final void clearAllReferences() {
		ensureReplicationActive();

		_referencesByObject.clear();
	}

	public final boolean hasReplicationReferenceAlready(Object obj) {
		ensureReplicationActive();

		return getCachedReference(obj) != null;
	}

	protected ReplicationReference getCachedReference(Object obj) {
		return (ReplicationReference) _referencesByObject.get(obj);
	}

	protected ReplicationReference createReference(Object obj, Db4oUUID uuid, long version) {
		ReplicationReference result = new ReplicationReferenceImpl(obj, uuid, version);
		_referencesByObject.put(obj, result);
		return result;
	}

	protected static void sleep(int i, String s) {
		System.out.println(s);
		try {
			Thread.sleep(i * 1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	protected ReplicationReference produceCollectionReferenceByReferencingObjUuid(ReplicationReference refObjRef, String fieldName, Session session) {
		Criteria criteria = session.createCriteria(ReplicationComponentIdentity.class);
		criteria.add(Restrictions.eq("referencingObjectUuidLongPart", new Long(refObjRef.uuid().getLongPart())));
		criteria.createCriteria("referencingObjectField").add(Restrictions.eq("referencingObjectFieldName", fieldName));
		criteria.createCriteria("provider").add(Restrictions.eq("bytes", refObjRef.uuid().getSignaturePart()));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		ReplicationReference out;
		if (count == 0) {
			out = null;
		} else if (count > 1) {
			throw new RuntimeException("Only one Record should exist for this peer");
		} else {
			ReplicationComponentIdentity rci = (ReplicationComponentIdentity) exisitings.get(0);

			final Object fieldValue = getFieldValue(refObjRef.object(), rci.getReferencingObjectField().getReferencingObjectFieldName());

			final ReplicationReference cachedReference = getCachedReference(fieldValue);
			if (cachedReference != null) return cachedReference;

			Db4oUUID fieldUuid = new Db4oUUID(rci.getUuidLongPart(), rci.getProvider().getBytes());
			out = createReference(fieldValue, fieldUuid, refObjRef.version());
		}

		return out;
	}

	protected Object getFieldValue(Object refObject, String referencingObjectFieldName) {
		final ReflectField declaredField = _reflector.forObject(refObject).getDeclaredField(referencingObjectFieldName);

		declaredField.setAccessible();
		final Object field = declaredField.get(refObject);
		if (field == null) throw new NullPointerException("field cannot be null");

		return field;
	}

	protected ReplicationReference produceCollectionReference(Object obj, Object referencingObj, String fieldName, Session session) {
		final ReplicationReference refObjRef = produceReference(referencingObj, null, null);

		if (refObjRef == null)
			return null;
		else {
			ReplicationReference existingReference = produceCollectionReferenceByReferencingObjUuid(refObjRef, fieldName, session);
			if (existingReference != null)
				return existingReference;
			else
				return createRefForCollection(obj, refObjRef, fieldName, uuidLongPartGenerator.next(), _currentVersion, session);
		}
	}

	protected ReplicationReference createRefForCollection(Object collection, ReplicationReference referencingObjRef,
			String fieldName, long uuidLong, long version, Session session) {
		final byte[] signaturePart = referencingObjRef.uuid().getSignaturePart();

		ReplicationComponentField rcf = produceReplicationComponentField(referencingObjRef.object().getClass().getName(), fieldName, session);
		ReplicationComponentIdentity rci = new ReplicationComponentIdentity();

		rci.setReferencingObjectField(rcf);
		rci.setReferencingObjectUuidLongPart(referencingObjRef.uuid().getLongPart());
		rci.setProvider(getProviderSignature(signaturePart, session));
		rci.setUuidLongPart(uuidLong);

		Db4oUUID uuid = new Db4oUUID(uuidLong, signaturePart);

		session.save(rci);
		return createReference(collection, uuid, version);
	}

	protected ReplicationComponentField produceReplicationComponentField(String referencingObjectClassName,
			String referencingObjectFieldName, Session session) {
		Criteria criteria = session.createCriteria(ReplicationComponentField.class);
		criteria.add(Restrictions.eq("referencingObjectClassName", referencingObjectClassName));
		criteria.add(Restrictions.eq("referencingObjectFieldName", referencingObjectFieldName));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		if (count == 0) {
			ReplicationComponentField out = new ReplicationComponentField();
			out.setReferencingObjectClassName(referencingObjectClassName);
			out.setReferencingObjectFieldName(referencingObjectFieldName);
			session.save(out);

			//Double-check, you know Hibernate sometimes fail to save an object.
			return produceReplicationComponentField(referencingObjectClassName, referencingObjectFieldName, session);
		} else if (count > 1) {
			throw new RuntimeException("Only one Record should exist for this peer");
		} else {
			return (ReplicationComponentField) exisitings.get(0);
		}
	}

	protected ReplicationReference referenceClonedCollection(Object obj, ReplicationReference counterpartReference,
			ReplicationReference referencingObjRef, String fieldName, Session session) {
		return createRefForCollection(obj, referencingObjRef, fieldName, counterpartReference.uuid().getLongPart(), counterpartReference.version(), session);
	}

	protected ReplicationProviderSignature getProviderSignature(byte[] signaturePart, Session session) {
		final List exisitingSigs = session.createCriteria(ReadonlyReplicationProviderSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.SIGNATURE_BYTE_ARRAY_COLUMN_NAME, signaturePart))
				.list();
		if (exisitingSigs.size() == 1)
			return (ReplicationProviderSignature) exisitingSigs.get(0);
		else if (exisitingSigs.size() == 0) return null;
		else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");
	}

	public boolean wasChangedSinceLastReplication(ReplicationReference reference) {
		ensureReplicationActive();
		if (_uuidsReplicatedInThisSession.contains(reference.uuid())) return false;
		return reference.version() > getLastReplicationVersion();
	}

	protected ReplicationReference produceCollectionReferenceByUUID(Db4oUUID uuid) {
//		Util.dumpTable(_session, "ReplicationProviderSignature");
//		Util.dumpTable(_session, "ReplicationComponentField");
//		Util.dumpTable(_session, "ReplicationComponentIdentity");

		Criteria criteria = getRefSession().createCriteria(ReplicationComponentIdentity.class);
		criteria.add(Restrictions.eq("uuidLongPart", new Long(uuid.getLongPart())));
		criteria.createCriteria("provider").add(Restrictions.eq("bytes", uuid.getSignaturePart()));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		ReplicationReference out;
		if (count == 0) {
			out = null;
		} else if (count > 1) {
			throw new RuntimeException("Only one Record should exist for this peer");
		} else {
			ReplicationComponentIdentity rci = (ReplicationComponentIdentity) exisitings.get(0);


			Db4oUUID refObjUuid = new Db4oUUID(rci.getReferencingObjectUuidLongPart(), rci.getProvider().getBytes());

			final Class hint;
			final ReplicationComponentField referencingObjectField = rci.getReferencingObjectField();
			try {
				hint = Class.forName(referencingObjectField.getReferencingObjectClassName());
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}

			final ReplicationReference refObjRef = produceReferenceByUUID(refObjUuid, hint);
			if (refObjRef == null) throw new NullPointerException("refObjRefcannot be null");

			final Object field = getFieldValue(refObjRef.object(), referencingObjectField.getReferencingObjectFieldName());

			ReplicationReference existing = getCachedReference(field);
			if (existing != null) return existing;

			out = createReference(field, uuid, refObjRef.version());
		}

		return out;
	}

	protected abstract Session getRefSession();

	public final ReplicationReference produceReferenceByUUID(final Db4oUUID uuid, Class hint) {
		ensureReplicationActive();

		if (uuid == null) throw new IllegalArgumentException("uuid cannot be null");
		if (hint == null) throw new IllegalArgumentException("hint cannot be null");

		if (_collectionHandler.canHandle(hint)) {
			return produceCollectionReferenceByUUID(uuid);
		} else {
			return produceObjectReferenceByUUID(uuid, hint);
		}
	}

	protected abstract ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint);

	public final ReplicationReference produceReference(Object obj, Object referencingObj, String fieldName) {
		ensureReplicationActive();

		ReplicationReference existing = getCachedReference(obj);

		if (existing != null) return existing;

		if (_collectionHandler.canHandle(obj)) {
			if (referencingObj == null)
				return null;

			return produceCollectionReference(obj, referencingObj, fieldName, getRefSession());
		} else {
			return produceObjectReference(obj);
		}
	}

	protected abstract ReplicationReference produceObjectReference(Object obj);

	public ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference,
			ReplicationReference referencingObjCounterPartRef, String fieldName) {
		ensureReplicationActive();

		if (obj == null) throw new NullPointerException("obj is null");
		if (counterpartReference == null) throw new NullPointerException("counterpartReference is null");

		if (_collectionHandler.canHandle(obj)) {

			if (referencingObjCounterPartRef == null || fieldName == null)
				return null;

			ReplicationReference cachedReference = getCachedReference(obj);
			if (cachedReference != null) return cachedReference;

			return referenceClonedCollection(obj, counterpartReference, referencingObjCounterPartRef, fieldName, getRefSession());
		} else {
			Db4oUUID uuid = counterpartReference.uuid();
			long version = counterpartReference.version();

			return createReference(obj, uuid, version);
		}
	}

	protected void initMySignature(Session session) {
		final Criteria criteria = session.createCriteria(MySignature.class);

		final List firstResult = criteria.list();
		final int mySigCount = firstResult.size();

		if (mySigCount < 1) {
			_mySig = MySignature.generateSignature();
			session.save(_mySig);
		} else if (mySigCount == 1) {
			_mySig = (MySignature) firstResult.get(0);
		} else {
			throw new RuntimeException("Number of MySignature should be exactly 1, but i got " + mySigCount);
		}
	}

	protected ReadonlyReplicationProviderSignature getById(long sigId, Session session) {
		return (ReadonlyReplicationProviderSignature) session.get(ReplicationProviderSignature.class, new Long(sigId));
	}

	protected void generateReplicationMetaData(Collection newObjects) {
		for (Iterator iterator = newObjects.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			Db4oUUID uuid = new Db4oUUID(uuidLongPartGenerator.next(), getSignature().getBytes());
			ReplicationReferenceImpl ref = new ReplicationReferenceImpl(o, uuid, _currentVersion);
			storeReplicationMetaData(ref);
		}
	}

	protected abstract void storeReplicationMetaData(ReplicationReference ref);

	protected PeerSignature getPeerSignature(byte[] bytes, Session session) {
		final List exisitingSigs = session.createCriteria(PeerSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.SIGNATURE_BYTE_ARRAY_COLUMN_NAME, bytes))
				.list();

		if (exisitingSigs.size() == 1)
			return (PeerSignature) exisitingSigs.get(0);
		else if (exisitingSigs.size() == 0) return null;
		else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");
	}

	protected ReplicationRecord getRecord(PeerSignature peerSignature) {
		Criteria criteria = getRefSession().createCriteria(ReplicationRecord.class).createCriteria("peerSignature").add(Restrictions.eq("id", new Long(peerSignature.getId())));

		final List exisitingRecords = criteria.list();
		int count = exisitingRecords.size();

		if (count == 0)
			throw new RuntimeException("Record not found. Hibernate was unable to persist the record in the last replication round");
		else if (count > 1)
			throw new RuntimeException("Only one Record should exist for this peer");
		else
			return (ReplicationRecord) exisitingRecords.get(0);
	}

	public void syncVersionWithPeer(long version) {
		ensureReplicationActive();

		if (version < Common.MIN_VERSION_NO)
			throw new RuntimeException("version must be great than " + Common.MIN_VERSION_NO);

		_replicationRecord.setVersion(version);
		getRefSession().saveOrUpdate(_replicationRecord);

		if (getRecord(_peerSignature).getVersion() != version)
			throw new RuntimeException("The version numbers of persisted record does not match the parameter");
	}

	public synchronized final void commitReplicationTransaction(long raisedDatabaseVersion) {
		ensureReplicationActive();
		commit();
		_uuidsReplicatedInThisSession.clear();
		_dirtyRefs.clear();
		_inReplication = false;
	}

	protected Collection loadObj(Collection<ChangedObjectId> changedObjectIds, Session sess) {
		Set out = new HashSet();

		for (Iterator<ChangedObjectId> iterator = changedObjectIds.iterator(); iterator.hasNext();) {
			ChangedObjectId changedObjectId = iterator.next();
			out.add(sess.load(changedObjectId.className, changedObjectId.hibernateId));
		}

		return out;
	}

	protected void init() {
		initMappedClasses();
		uuidLongPartGenerator = new UuidLongPartGenerator(getRefSession());
		initMySignature(getRefSession());
	}

	protected abstract void initMappedClasses();

	protected FlushEventListener[] createFlushEventListeners(FlushEventListener[] defaultListeners) {
		if (defaultListeners == null) {
			return new FlushEventListener[]{myFlushEventListener};
		} else {
			FlushEventListener[] out;
			final int count = defaultListeners.length;
			out = new FlushEventListener[count + 1];
			System.arraycopy(defaultListeners, 0, out, 0, count);
			out[count] = myFlushEventListener;
			return out;
		}
	}

	protected PostUpdateEventListener[] createPostUpdateListeners(PostUpdateEventListener[] defaultListeners) {
		if (defaultListeners == null) {
			return new PostUpdateEventListener[]{myPostUpdateEventListener};
		} else {
			PostUpdateEventListener[] out;
			final int count = defaultListeners.length;
			out = new PostUpdateEventListener[count + 1];
			System.arraycopy(defaultListeners, 0, out, 0, count);
			out[count] = myPostUpdateEventListener;
			return out;
		}
	}
}
