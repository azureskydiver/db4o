package com.db4o.replication.hibernate.impl;

import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.Visitor4;
import com.db4o.inside.replication.CollectionHandler;
import com.db4o.inside.replication.CollectionHandlerImpl;
import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import com.db4o.inside.replication.ReplicationReference;
import com.db4o.inside.replication.ReplicationReflector;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.Reflector;
import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.cfg.RefConfig;
import com.db4o.replication.hibernate.metadata.DeletedObject;
import com.db4o.replication.hibernate.metadata.PeerSignature;
import com.db4o.replication.hibernate.metadata.ReplicationComponentField;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.EventListeners;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.mapping.PersistentClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public abstract class AbstractReplicationProvider implements HibernateReplicationProvider {
// ------------------------------ FIELDS ------------------------------

	protected RefConfig _refCfg;

	protected ObjectConfig _objectConfig;

	protected String _name;

	protected Session _session;

	protected SessionFactory _sessionFactory;

	protected Transaction _transaction;

	protected ObjectReferenceMap _objRefs = new ObjectReferenceMap();

	protected boolean _alive = false;

	protected UuidGenerator uuidGenerator = new UuidGenerator();

	/**
	 * The Signature of the peer in the current Transaction.
	 */
	private PeerSignature _peerSignature;

	private Set<PersistentClass> _mappedClasses;

	private FlushEventListener myFlushEventListener = new MyFlushEventListener();

	private PostUpdateEventListener myPostUpdateEventListener = new MyPostUpdateEventListener();

	private PreDeleteEventListener myPreDeleteEventListener = new MyPreDeleteEventListener();

	private PostInsertEventListener objectInsertedListener = new MyObjectInsertedListener();

	/**
	 * The ReplicationRecord of {@link #_peerSignature}.
	 */
	private ReplicationRecord _replicationRecord;

	/**
	 * Current transaction number = {@link #getLastReplicationVersion()} + 1. The
	 * minimum version number is 1, when this database is never replicated with
	 * other peers.
	 */
	private long _currentVersion;

	private final CollectionHandler _collectionHandler = new CollectionHandlerImpl();

	private Set _uuidsReplicatedInThisSession = new HashSet();

	private Reflector _reflector = ReplicationReflector.getInstance().reflector();

	/**
	 * Objects which meta data not yet updated.
	 */
	private Set<ReplicationReference> _dirtyRefs = new HashSet();

	private boolean _inReplication = false;

// ------------------------ CANONICAL METHODS ------------------------

	public final String toString() {
		return "name = " + _name;
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface HibernateReplicationProvider ---------------------

	public final Configuration getConfiguration() {
		return getObjectConfig().getConfiguration();
	}

	public final Session getSession() {
		return _session;
	}

// --------------------- Interface ReplicationProvider ---------------------

	public final ObjectSet objectsChangedSinceLastReplication() {
		ensureReplicationActive();

		getSession().flush();

		Set out = new HashSet();
		for (PersistentClass persistentClass : _mappedClasses)
			out.addAll(getChangedObjectsSinceLastReplication(persistentClass));

		return new ObjectSetCollectionFacade(out);
	}

	public final ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		ensureReplicationActive();
		getSession().flush();

		PersistentClass persistentClass = getRefConfig().getConfiguration().getClassMapping(clazz.getName());
		return new ObjectSetCollectionFacade(getChangedObjectsSinceLastReplication(persistentClass));
	}

	public final ObjectSet uuidsDeletedSinceLastReplication() {
		List<DeletedObject> results = getSession().createCriteria(DeletedObject.class).list();
		Collection<Db4oUUID> out = new HashSet<Db4oUUID>(results.size());

		for (DeletedObject doo : results) {
			out.add(Util.translate(doo));
		}
		return new ObjectSetCollectionFacade(out);
	}

// --------------------- Interface ReplicationProviderInside ---------------------

	public final void clearAllReferences() {
		ensureReplicationActive();

		_objRefs.clear();
	}

	public final synchronized void commitReplicationTransaction(long raisedDatabaseVersion) {
		ensureReplicationActive();
		clearDeletedUuids();
		commit();
		_uuidsReplicatedInThisSession.clear();
		_dirtyRefs.clear();
		_inReplication = false;
	}

	public final synchronized void destroy() {
		_alive = false;

		_session.close();
		_sessionFactory.close();

		_transaction = null;
		_session = null;
		_sessionFactory = null;

		_mappedClasses = null;
		_dirtyRefs = null;

		_objRefs = null;
		_uuidsReplicatedInThisSession = null;

		_reflector = null;

		destroyListeners();
	}

	public final long getCurrentVersion() {
		ensureReplicationActive();

		return _currentVersion;
	}

	public final Object getMonitor() {
		return this;
	}

	public final ReadonlyReplicationProviderSignature getSignature() {
		return Util.genMySignature(getSession());
	}

	public final boolean hasReplicationReferenceAlready(Object obj) {
		ensureReplicationActive();

		return _objRefs.get(obj) != null;
	}

	public final ReplicationReference produceReference(Object obj, Object referencingObj, String fieldName) {
		ensureReplicationActive();

		ReplicationReference existing = _objRefs.get(obj);

		if (existing != null) return existing;

		if (_collectionHandler.canHandle(obj)) {
			if (referencingObj == null)
				return null;

			return produceCollectionReference(obj, referencingObj, fieldName);
		} else {
			return produceObjectReference(obj);
		}
	}

	public ReplicationReference produceReferenceByUUID(final Db4oUUID uuid, Class hint) {
		ensureReplicationActive();

		if (uuid == null) throw new IllegalArgumentException("uuid cannot be null");
		if (hint == null) throw new IllegalArgumentException("hint cannot be null");

		if (_collectionHandler.canHandle(hint)) {
			return produceCollectionReferenceByUUID(uuid);
		} else {
			return produceObjectReferenceByUUID(uuid, hint);
		}
	}

	public final ReplicationReference referenceNewObject(Object obj, ReplicationReference counterpartReference,
			ReplicationReference referencingObjCounterPartRef, String fieldName) {
		ensureReplicationActive();

		if (obj == null) throw new NullPointerException("obj is null");
		if (counterpartReference == null) throw new NullPointerException("counterpartReference is null");

		if (_collectionHandler.canHandle(obj)) {
			if (referencingObjCounterPartRef == null || fieldName == null)
				return null;

			ReplicationReference cachedReference = _objRefs.get(obj);
			if (cachedReference != null) return cachedReference;

			return referenceClonedCollection(obj, counterpartReference, referencingObjCounterPartRef, fieldName);
		} else {
			Db4oUUID uuid = counterpartReference.uuid();
			long version = counterpartReference.version();

			return _objRefs.put(obj, uuid, version);
		}
	}

	public final synchronized void rollbackReplication() {
		ensureReplicationActive();

		getObjectTransaction().rollback();
		clearSession();

		_transaction = getSession().beginTransaction();
		clearAllReferences();
		_dirtyRefs.clear();
		_uuidsReplicatedInThisSession.clear();
		_inReplication = false;
	}

	public final void startReplicationTransaction(ReadonlyReplicationProviderSignature aPeerSignature) {
		ensureReplicationInActive();
		clearSession();

		byte[] peerSigBytes = aPeerSignature.getBytes();

		if (Arrays.equals(peerSigBytes, getSignature().getBytes()))
			throw new RuntimeException("peerSigBytes must not equal to my own sig");

		getObjectTransaction().commit();
		_transaction = getSession().beginTransaction();

		initPeerSigAndRecord(peerSigBytes);

		_currentVersion = Util.getMaxVersion(getSession().connection()) + 1;

		uuidGenerator.reset(getSession());

		_inReplication = true;
	}

	public final void storeReplica(Object entity) {
		ensureReplicationActive();

		//Hibernate does not treat Collection as 1st class object, so storing a Collection is no-op
		if (_collectionHandler.canHandle(entity)) return;

		ReplicationReference ref = _objRefs.get(entity);
		if (ref == null) throw new RuntimeException("Reference should always be available before storeReplica");

		_uuidsReplicatedInThisSession.add(ref.uuid());

		final Session s = getSession();
		if (s.contains(entity)) {
			s.update(entity);
		} else {
			s.save(entity);
		}

		_dirtyRefs.add(ref);

		getSession().flush();
	}

	public final void syncVersionWithPeer(long version) {
		ensureReplicationActive();

		if (version < Constants.MIN_VERSION_NO)
			throw new RuntimeException("version must be great than " + Constants.MIN_VERSION_NO);

		_replicationRecord.setVersion(version);
		getSession().saveOrUpdate(_replicationRecord);
		getSession().flush();
		if (getRecord(_peerSignature).getVersion() != version)
			throw new RuntimeException("The version numbers of persisted record does not match the parameter");
	}

	public final void visitCachedReferences(Visitor4 visitor) {
		ensureReplicationActive();

		_objRefs.visitEntries(visitor);
	}

	public final boolean wasChangedSinceLastReplication(ReplicationReference reference) {
		ensureReplicationActive();
		if (_uuidsReplicatedInThisSession.contains(reference.uuid())) return false;
		return reference.version() > getLastReplicationVersion();
	}

// --------------------- Interface SimpleObjectContainer ---------------------

	public final void activate(Object object) {
		Hibernate.initialize(object);
	}

	public final void commit() {
		getObjectTransaction().commit();
		clearSession();
		_transaction = getSession().beginTransaction();
		uuidGenerator.reset(getSession());
	}

	public final void delete(Object obj) {
		getSession().delete(obj);
	}

	public final void deleteAllInstances(Class clazz) {
		ensureReplicationInActive();
		List col = getSession().createCriteria(clazz).list();
		for (Object o : col)
			delete(o);
	}

	public final String getName() {
		return _name;
	}

	public final ObjectSet getStoredObjects(Class aClass) {
		if (_collectionHandler.canHandle(aClass))
			throw new IllegalArgumentException("Hibernate does not query by Collection");

		getSession().flush();

		return new ObjectSetCollectionFacade(getSession().createCriteria(aClass).list());
	}

	public final void storeNew(Object object) {
		ensureReplicationInActive();
		Session s = getSession();
		s.save(object);
		s.flush();
	}

	public final void update(Object obj) {
		ensureReplicationInActive();
		if (!_collectionHandler.canHandle(obj)) {
			getSession().flush();
			getSession().update(obj);
			getSession().flush();
		}
	}

// -------------------------- OTHER METHODS --------------------------

	protected abstract Collection getChangedObjectsSinceLastReplication(PersistentClass persistentClass);

	protected abstract RefConfig getRefConfig();

	protected abstract Uuid getUuid(Object obj);

	protected abstract void incrementObjectVersion(PostUpdateEvent event);

	protected abstract void objectInserted(PostInsertEvent event);

	protected abstract ReplicationReference produceObjectReference(Object obj);

	protected abstract ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid, Class hint);

	protected abstract void saveOrUpdateReplicaMetadata(ReplicationReference ref);

	protected void ensureReplicationActive() {
		ensureAlive();
		if (!isReplicationActive())
			throw new UnsupportedOperationException("Method not supported because replication transaction is not active");
	}

	protected void ensureReplicationInActive() {
		ensureAlive();
		if (isReplicationActive())
			throw new UnsupportedOperationException("Method not supported because replication transaction is active");
	}

	protected long getLastReplicationVersion() {
		ensureReplicationActive();

		return getCurrentVersion() - 1;
	}

	protected ObjectConfig getObjectConfig() {
		return _objectConfig;
	}

	protected ReplicationProviderSignature getProviderSignature(byte[] signaturePart) {
		final List exisitingSigs = getSession().createCriteria(ReadonlyReplicationProviderSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.BYTES, signaturePart))
				.list();
		if (exisitingSigs.size() == 1)
			return (ReplicationProviderSignature) exisitingSigs.get(0);
		else if (exisitingSigs.size() == 0) return null;
		else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");
	}

	protected final RefConfig getRefCfg() {
		return _refCfg;
	}

	protected final void init() {
		initMappedClasses();
		uuidGenerator.reset(getSession());
	}

	protected final void initEventListeners() {
		Configuration cfg = getObjectConfig().getConfiguration();
		cfg.setInterceptor(EmptyInterceptor.INSTANCE);
		EventListeners eventListeners = cfg.getEventListeners();

		eventListeners.setFlushEventListeners(createFlushEventListeners(eventListeners.getFlushEventListeners()));
		eventListeners.setPostUpdateEventListeners(new PostUpdateEventListener[]{myPostUpdateEventListener});
		eventListeners.setPreDeleteEventListeners(new PreDeleteEventListener[]{myPreDeleteEventListener});

		eventListeners.setPostInsertEventListeners(new PostInsertEventListener[]{objectInsertedListener});
	}

	protected final Collection loadObject(Collection<HibernateObjectId> changedObjectIds) {
		Set out = new HashSet();

		for (HibernateObjectId hibernateObjectId : changedObjectIds)
			out.add(loadObject(hibernateObjectId));

		return out;
	}

	protected Object loadObject(HibernateObjectId hibernateObjectId) {
		return getSession().load(hibernateObjectId.className, hibernateObjectId.hibernateId);
	}

	protected void objectToBeDeleted(PreDeleteEvent event) {
		deleteReplicationComponentIdentity(event);
		addDeletedObject(event);
	}

	protected Db4oUUID translate(Uuid uuid) {
		return new Db4oUUID(uuid.getLongPart(), uuid.getProvider().getBytes());
	}

	private void addDeletedObject(PreDeleteEvent event) {
		Uuid uuid = getUuid(event.getEntity());
		DeletedObject deletedObject = new DeletedObject();
		deletedObject.setUuid(uuid);
		getSession().save(deletedObject);
	}

	private void clearDeletedUuids() {
		getSession().createQuery("delete from " + DeletedObject.TABLE_NAME).executeUpdate();
	}

	private void clearSession() {
		getSession().clear();
	}

	private FlushEventListener[] createFlushEventListeners(FlushEventListener[] defaultListeners) {
		FlushEventListener[] out;
		final int count = defaultListeners.length;
		out = new FlushEventListener[count + 1];
		System.arraycopy(defaultListeners, 0, out, 0, count);
		out[count] = myFlushEventListener;
		return out;
	}

	private ReplicationReference createRefForCollection(Object collection, ReplicationReference referencingObjRef,
			String fieldName, long uuidLong, long version) {
		final byte[] signaturePart = referencingObjRef.uuid().getSignaturePart();

		ReplicationComponentField rcf = produceReplicationComponentField(referencingObjRef.object().getClass().getName(), fieldName);
		ReplicationComponentIdentity rci = new ReplicationComponentIdentity();

		rci.setReferencingObjectField(rcf);
		rci.setReferencingObjectUuidLongPart(referencingObjRef.uuid().getLongPart());
		rci.setProvider(getProviderSignature(signaturePart));
		rci.setUuidLongPart(uuidLong);

		Db4oUUID uuid = new Db4oUUID(uuidLong, signaturePart);

		getSession().save(rci);
		return _objRefs.put(collection, uuid, version);
	}

	private void deleteReplicationComponentIdentity(PreDeleteEvent event) {
		ReplicationReference replicationReference = produceObjectReference(event.getEntity());
		ReplicationComponentIdentity rci = Util.getReplicationComponentIdentityByRefObjUuid(getSession(), translate(replicationReference.uuid()));

		if (rci != null) delete(rci);
	}

	private void destroyListeners() {
		EventListeners eventListeners = getObjectConfig().getConfiguration().getEventListeners();
		FlushEventListener[] o1 = eventListeners.getFlushEventListeners();
		FlushEventListener[] r1 = (FlushEventListener[]) ArrayUtils.removeElement(
				o1, myFlushEventListener);
		if ((o1.length - r1.length) != 1)
			throw new RuntimeException("can't remove");

		eventListeners.setFlushEventListeners(r1);
		myFlushEventListener = null;

		PostUpdateEventListener[] o2 = eventListeners.getPostUpdateEventListeners();
		PostUpdateEventListener[] r2 = (PostUpdateEventListener[]) ArrayUtils.removeElement(
				o2, myPostUpdateEventListener);
		if ((o2.length - r2.length) != 1)
			throw new RuntimeException("can't remove");
		eventListeners.setPostUpdateEventListeners(r2);
		myPostUpdateEventListener = null;

		PreDeleteEventListener[] o3 = eventListeners.getPreDeleteEventListeners();
		PreDeleteEventListener[] r3 = (PreDeleteEventListener[]) ArrayUtils.removeElement(
				o3, myPreDeleteEventListener);
		if ((o3.length - r3.length) != 1)
			throw new RuntimeException("can't remove");
		eventListeners.setPreDeleteEventListeners(r3);
		myPreDeleteEventListener = null;

		PostInsertEventListener[] o4 = eventListeners.getPostInsertEventListeners();
		PostInsertEventListener[] r4 = (PostInsertEventListener[]) ArrayUtils.removeElement(
				o4, objectInsertedListener);
		if ((o4.length - r4.length) != 1)
			throw new RuntimeException("can't remove");

		eventListeners.setPostInsertEventListeners(r4);
		objectInsertedListener = null;
	}

	private void ensureAlive() {
		if (!_alive)
			throw new UnsupportedOperationException("This provider is dead because #destroy() is called");
	}

	private Object getFieldValue(Object refObject, String referencingObjectFieldName) {
		final ReflectField declaredField = _reflector.forObject(refObject).getDeclaredField(referencingObjectFieldName);

		declaredField.setAccessible();
		final Object field = declaredField.get(refObject);
		if (field == null) throw new NullPointerException("field cannot be null");

		return field;
	}

	private Transaction getObjectTransaction() {
		return _transaction;
	}

	private PeerSignature getPeerSignature(byte[] bytes) {
		final List exisitingSigs = getSession().createCriteria(PeerSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.BYTES, bytes))
				.list();

		if (exisitingSigs.size() == 1)
			return (PeerSignature) exisitingSigs.get(0);
		else if (exisitingSigs.size() == 0) return null;
		else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");
	}

	private ReplicationRecord getRecord(PeerSignature peerSignature) {
		Criteria criteria = getSession().createCriteria(ReplicationRecord.class).createCriteria("peerSignature").add(Restrictions.eq("id", new Long(peerSignature.getId())));

		final List exisitingRecords = criteria.list();
		int count = exisitingRecords.size();

		if (count == 0)
			throw new RuntimeException("Record not found. Hibernate was unable to persist the record in the last replication round");
		else if (count > 1)
			throw new RuntimeException("Only one Record should exist for this peer");
		else
			return (ReplicationRecord) exisitingRecords.get(0);
	}

	private void initMappedClasses() {
		_mappedClasses = new HashSet();

		Iterator classMappings = getObjectConfig().getConfiguration().getClassMappings();
		while (classMappings.hasNext()) {
			PersistentClass persistentClass = (PersistentClass) classMappings.next();
			Class claxx = persistentClass.getMappedClass();

			if (Util.skip(claxx))
				continue;

			_mappedClasses.add(persistentClass);
		}
	}

	private void initPeerSigAndRecord(byte[] peerSigBytes) {
		PeerSignature existingPeerSignature = getPeerSignature(peerSigBytes);
		if (existingPeerSignature == null) {
			this._peerSignature = new PeerSignature(peerSigBytes);
			getSession().save(this._peerSignature);
			getSession().flush();
			if (getPeerSignature(peerSigBytes) == null)
				throw new RuntimeException("Cannot insert existingPeerSignature");
			_replicationRecord = new ReplicationRecord();
			_replicationRecord.setPeerSignature(_peerSignature);
		} else {
			this._peerSignature = existingPeerSignature;
			_replicationRecord = getRecord(_peerSignature);
		}
	}

	private boolean isReplicationActive() {
		return _inReplication;
	}

	private ReplicationReference produceCollectionReference(Object obj, Object referencingObj, String fieldName) {
		final ReplicationReference refObjRef = produceReference(referencingObj, null, null);

		if (refObjRef == null)
			return null;
		else {
			ReplicationReference existingReference = produceCollectionReferenceByReferencingObjUuid(refObjRef, fieldName);
			if (existingReference != null)
				return existingReference;
			else
				return createRefForCollection(obj, refObjRef, fieldName, uuidGenerator.next().getLongPart(), _currentVersion);
		}
	}

	private ReplicationReference produceCollectionReferenceByReferencingObjUuid(ReplicationReference refObjRef, String fieldName) {
		Criteria criteria = getSession().createCriteria(ReplicationComponentIdentity.class);
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

			final ReplicationReference cachedReference = _objRefs.get(fieldValue);
			if (cachedReference != null) return cachedReference;

			Db4oUUID fieldUuid = new Db4oUUID(rci.getUuidLongPart(), rci.getProvider().getBytes());
			out = _objRefs.put(fieldValue, fieldUuid, refObjRef.version());
		}

		return out;
	}

	private ReplicationReference produceCollectionReferenceByUUID(Db4oUUID uuid) {
		Criteria criteria = getSession().createCriteria(ReplicationComponentIdentity.class);
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

			ReplicationReference existing = _objRefs.get(field);
			if (existing != null) return existing;

			out = _objRefs.put(field, uuid, refObjRef.version());
		}

		return out;
	}

	private ReplicationComponentField produceReplicationComponentField(String referencingObjectClassName,
			String referencingObjectFieldName) {
		Criteria criteria = getSession().createCriteria(ReplicationComponentField.class);
		criteria.add(Restrictions.eq("referencingObjectClassName", referencingObjectClassName));
		criteria.add(Restrictions.eq("referencingObjectFieldName", referencingObjectFieldName));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		if (count == 0) {
			ReplicationComponentField out = new ReplicationComponentField();
			out.setReferencingObjectClassName(referencingObjectClassName);
			out.setReferencingObjectFieldName(referencingObjectFieldName);
			getSession().save(out);

			//Double-check, you know Hibernate sometimes fail to save an object.
			return produceReplicationComponentField(referencingObjectClassName, referencingObjectFieldName);
		} else if (count > 1) {
			throw new RuntimeException("Only one Record should exist for this peer");
		} else {
			return (ReplicationComponentField) exisitings.get(0);
		}
	}

	private ReplicationReference referenceClonedCollection(Object obj, ReplicationReference counterpartReference,
			ReplicationReference referencingObjRef, String fieldName) {
		return createRefForCollection(obj, referencingObjRef, fieldName, counterpartReference.uuid().getLongPart(), counterpartReference.version());
	}

	private Uuid translate(Db4oUUID db4oUUID) {
		Uuid uuid = new Uuid();
		uuid.setLongPart(db4oUUID.getLongPart());
		uuid.setProvider(getProviderSignature(db4oUUID.getSignaturePart()));
		return uuid;
	}

// -------------------------- INNER CLASSES --------------------------

	final class MyFlushEventListener implements FlushEventListener {
		public final void onFlush(FlushEvent event) throws HibernateException {
			if (!isReplicationActive()) return;
			for (Iterator iterator = _dirtyRefs.iterator(); iterator.hasNext();) {
				ReplicationReference ref = (ReplicationReference) iterator.next();
				iterator.remove();
				saveOrUpdateReplicaMetadata(ref);
			}
		}
	}

	final class MyPostUpdateEventListener implements PostUpdateEventListener {
		public final void onPostUpdate(PostUpdateEvent event) {
			synchronized (AbstractReplicationProvider.this) {
				if (isReplicationActive()) return;

				Object entity = event.getEntity();

				if (Util.skip(entity)) return;

				incrementObjectVersion(event);
			}
		}
	}

	final class MyPreDeleteEventListener implements PreDeleteEventListener {
		public boolean onPreDelete(PreDeleteEvent event) {
			boolean ret = false;

			if (isReplicationActive()) return ret;
			if (Util.skip(event.getEntity())) return ret;

			objectToBeDeleted(event);

			return ret;
		}
	}

	final class MyObjectInsertedListener implements PostInsertEventListener {
		public void onPostInsert(PostInsertEvent event) {
			if (isReplicationActive()) return;

			Object entity = event.getEntity();

			if (Util.skip(entity)) return;

			objectInserted(event);
		}
	}
}
