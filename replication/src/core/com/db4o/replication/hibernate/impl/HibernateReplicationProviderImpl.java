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
import com.db4o.replication.hibernate.ObjectLifeCycleEventsListener;
import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.PeerSignature;
import com.db4o.replication.hibernate.metadata.ReplicationComponentField;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.EmptyInterceptor;
import org.hibernate.FlushMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.event.EventListeners;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.mapping.PersistentClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public final class HibernateReplicationProviderImpl implements HibernateReplicationProvider {
// ------------------------------ FIELDS ------------------------------

	private Configuration _cfg;

	private final String _name;

	private Session _session;

	private SessionFactory _sessionFactory;

	private Transaction _transaction;

	private ObjectReferenceMap _objRefs = new ObjectReferenceMap();

	private boolean _alive = false;

	private ObjectLifeCycleEventsListener _lifeCycleEventsListener;

	/**
	 * The Signature of the peer in the current Transaction.
	 */
	private PeerSignature _peerSignature;

	private FlushEventListener myFlushEventListener = new MyFlushEventListener();

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

	private Set<Db4oUUID> _uuidsReplicatedInThisSession = new HashSet();

	private Reflector _reflector = ReplicationReflector.getInstance().reflector();

	/**
	 * Objects which meta data not yet updated.
	 */
	private Set<ReplicationReference> _dirtyRefs = new HashSet();

	private boolean _inReplication = false;

// --------------------------- CONSTRUCTORS ---------------------------

	public HibernateReplicationProviderImpl(Configuration cfg) {
		this(cfg, null);
	}

	public HibernateReplicationProviderImpl(Configuration cfg, String name) {
		_name = name;

		this._cfg = ReplicationConfiguration.decorate(cfg);

		Util.initUuidLongPartSequence(cfg);
		_lifeCycleEventsListener = new MyObjectLifeCycleEventsListener();
		_lifeCycleEventsListener.configure(cfg);

		new TablesCreatorImpl(_cfg).createTables();

		_cfg.setInterceptor(EmptyInterceptor.INSTANCE);
		EventListeners eventListeners = _cfg.getEventListeners();
		FlushEventListener[] defaultListeners = _cfg.getEventListeners().getFlushEventListeners();
		FlushEventListener[] out;
		final int count = defaultListeners.length;
		out = new FlushEventListener[count + 1];
		System.arraycopy(defaultListeners, 0, out, 0, count);
		out[count] = myFlushEventListener;
		eventListeners.setFlushEventListeners(out);

		_sessionFactory = getConfiguration().buildSessionFactory();
		_session = _sessionFactory.openSession();
		_session.setFlushMode(FlushMode.COMMIT);
		_transaction = _session.beginTransaction();

		_lifeCycleEventsListener.install(getSession(), cfg);

		_alive = true;
	}

// ------------------------ CANONICAL METHODS ------------------------

	public final String toString() {
		return "name = " + _name;
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface HibernateReplicationProvider ---------------------

	public final Configuration getConfiguration() {
		return _cfg;
	}

	public final Session getSession() {
		return _session;
	}

// --------------------- Interface ReplicationProvider ---------------------

	public final ObjectSet objectsChangedSinceLastReplication() {
		ensureReplicationActive();

		getSession().flush();

		Set<PersistentClass> mappedClasses = new HashSet();

		Iterator classMappings = getConfiguration().getClassMappings();
		while (classMappings.hasNext()) {
			PersistentClass persistentClass = (PersistentClass) classMappings.next();
			Class claxx = persistentClass.getMappedClass();

			if (Util.skip(claxx))
				continue;

			mappedClasses.add(persistentClass);
		}

		Set out = new HashSet();
		for (PersistentClass persistentClass : mappedClasses)
			out.addAll(getChangedObjectsSinceLastReplication(persistentClass));

		return new ObjectSetCollectionFacade(out);
	}

	public final ObjectSet objectsChangedSinceLastReplication(Class clazz) {
		ensureReplicationActive();
		getSession().flush();

		PersistentClass persistentClass = getConfiguration().getClassMapping(clazz.getName());
		return new ObjectSetCollectionFacade(getChangedObjectsSinceLastReplication(persistentClass));
	}

	public final ObjectSet uuidsDeletedSinceLastReplication() {
		Criteria criteria = getSession().createCriteria(ObjectReference.class);
		criteria.add(Restrictions.eq(ObjectReference.DELETED, true));

		List<ObjectReference> results = criteria.list();
		Collection<Db4oUUID> out = new HashSet<Db4oUUID>(results.size());
		for (ObjectReference of : results) {
			out.add(Util.translate(of.getUuid()));
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

		String sql = "delete " + ObjectReference.TABLE_NAME + " o where o." + ObjectReference.DELETED + " = 'T'";
		getSession().createQuery(sql).executeUpdate();

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

		_dirtyRefs = null;

		_objRefs = null;
		_uuidsReplicatedInThisSession = null;

		_reflector = null;

		EventListeners eventListeners = getConfiguration().getEventListeners();
		FlushEventListener[] o1 = eventListeners.getFlushEventListeners();
		FlushEventListener[] r1 = (FlushEventListener[]) ArrayUtils.removeElement(
				o1, myFlushEventListener);
		if ((o1.length - r1.length) != 1)
			throw new RuntimeException("can't remove");

		eventListeners.setFlushEventListeners(r1);
		myFlushEventListener = null;

		_lifeCycleEventsListener.destroy();
		_lifeCycleEventsListener = null;

		_cfg = null;
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

	public final ReplicationReference produceReferenceByUUID(final Db4oUUID uuid, Class hint) {
		ensureReplicationActive();

		if (uuid == null) throw new IllegalArgumentException("uuid cannot be null");
		if (hint == null) throw new IllegalArgumentException("hint cannot be null");

		if (_collectionHandler.canHandle(hint)) {
			return produceCollectionReferenceByUUID(uuid);
		} else {
			return produceObjectReferenceByUUID(uuid);
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

			return createRefForCollection(obj, referencingObjCounterPartRef, fieldName, counterpartReference.uuid().getLongPart(), counterpartReference.version());
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

		final List exisitingSigs = getSession().createCriteria(PeerSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.BYTES, peerSigBytes)).list();

		if (exisitingSigs.size() == 1) {
			_peerSignature = (PeerSignature) exisitingSigs.get(0);
			_replicationRecord = (ReplicationRecord) getSession().createCriteria(ReplicationRecord.class)
					.createCriteria("peerSignature").add(Restrictions.eq("id", _peerSignature.getId())).list().get(0);
		} else if (exisitingSigs.size() == 0) {
			_peerSignature = new PeerSignature(peerSigBytes);
			getSession().save(_peerSignature);
			getSession().flush();

			_replicationRecord = new ReplicationRecord();
			_replicationRecord.setPeerSignature(_peerSignature);
		} else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");

		_currentVersion = Util.getMaxVersion(getSession().connection()) + 1;

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
		getSession().flush();
		getObjectTransaction().commit();
		clearSession();
		_transaction = getSession().beginTransaction();
		//uuidGenerator.reset(getSession());
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

	private void clearSession() {
		getSession().clear();
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

	private void ensureAlive() {
		if (!_alive)
			throw new UnsupportedOperationException("This provider is dead because #destroy() is called");
	}

	private void ensureReplicationActive() {
		ensureAlive();
		if (!isReplicationActive())
			throw new UnsupportedOperationException("Method not supported because replication transaction is not active");
	}

	private void ensureReplicationInActive() {
		ensureAlive();
		if (isReplicationActive())
			throw new UnsupportedOperationException("Method not supported because replication transaction is active");
	}

	private Collection getChangedObjectsSinceLastReplication(PersistentClass persistentClass) {
		Criteria criteria = getSession().createCriteria(ObjectReference.class);
		criteria.add(Restrictions.gt(ObjectReference.VERSION, getLastReplicationVersion()));
		Disjunction disjunction = Restrictions.disjunction();

		List<String> names = new ArrayList<String>();
		names.add(persistentClass.getClassName());
		if (persistentClass.hasSubclasses()) {
			final Iterator it = persistentClass.getSubclassClosureIterator();
			while (it.hasNext()) {
				PersistentClass subC = (PersistentClass) it.next();
				names.add(subC.getClassName());
			}
		}

		for (String s : names)
			disjunction.add(Restrictions.eq(ObjectReference.CLASS_NAME, s));

		criteria.add(disjunction);

		Collection<HibernateObjectId> ids = new HashSet();
		for (Object o : criteria.list()) {
			ObjectReference ref = (ObjectReference) o;
			final HibernateObjectId hibernateObjectId = new HibernateObjectId(ref.getObjectId(), persistentClass.getRootClass().getClassName());
			ids.add(hibernateObjectId);
		}

		return loadObjects(ids);
	}

	private Object getFieldValue(Object refObject, String referencingObjectFieldName) {
		final ReflectField declaredField = _reflector.forObject(refObject).getDeclaredField(referencingObjectFieldName);

		declaredField.setAccessible();
		final Object field = declaredField.get(refObject);
		if (field == null) throw new NullPointerException("field cannot be null");

		return field;
	}

	private long getLastReplicationVersion() {
		ensureReplicationActive();

		return getCurrentVersion() - 1;
	}

	private Transaction getObjectTransaction() {
		return _transaction;
	}

	private ReplicationProviderSignature getProviderSignature(byte[] signaturePart) {
		final List exisitingSigs = getSession().createCriteria(ReplicationProviderSignature.class)
				.add(Restrictions.eq(ReplicationProviderSignature.BYTES, signaturePart))
				.list();
		if (exisitingSigs.size() == 1)
			return (ReplicationProviderSignature) exisitingSigs.get(0);
		else if (exisitingSigs.size() == 0) return null;
		else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");
	}

	private boolean isReplicationActive() {
		return _inReplication;
	}

	private Object loadObject(HibernateObjectId hibernateObjectId) {
		return getSession().load(hibernateObjectId.className, hibernateObjectId.hibernateId);
	}

	private Collection loadObjects(Collection<HibernateObjectId> changedObjectIds) {
		Set out = new HashSet();

		for (HibernateObjectId hibernateObjectId : changedObjectIds)
			out.add(loadObject(hibernateObjectId));

		return out;
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
				return createRefForCollection(obj, refObjRef, fieldName, UuidGenerator.next(getSession()).getLongPart(), _currentVersion);
		}
	}

	private ReplicationReference produceCollectionReferenceByReferencingObjUuid(ReplicationReference refObjRef, String fieldName) {
		Criteria criteria = getSession().createCriteria(ReplicationComponentIdentity.class);
		criteria.add(Restrictions.eq("referencingObjectUuidLongPart", refObjRef.uuid().getLongPart()));
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
		criteria.add(Restrictions.eq("uuidLongPart", uuid.getLongPart()));
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

	private ReplicationReference produceObjectReference(Object obj) {
		if (!getSession().contains(obj)) return null;

		final ObjectReference ref = Util.getObjectReferenceById(getSession(), obj);

		if (ref == null) throw new RuntimeException("ObjectReference must exist for " + obj);

		Uuid uuid = ref.getUuid();
		return _objRefs.put(obj, new Db4oUUID(uuid.getLongPart(), uuid.getProvider().getBytes()), ref.getVersion());
	}

	private ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid) {
		Uuid tmp = new Uuid();
		tmp.setLongPart(uuid.getLongPart());
		tmp.setProvider(getProviderSignature(uuid.getSignaturePart()));

		ObjectReference of = Util.getByUUID(getSession(), tmp);
		if (of == null)
			return null;
		else {
			Object obj = getSession().load(of.getClassName(), of.getObjectId());
			return _objRefs.put(obj, uuid, of.getVersion());
		}
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

// -------------------------- INNER CLASSES --------------------------

	private final class MyFlushEventListener implements FlushEventListener {
		public final void onFlush(FlushEvent event) throws HibernateException {
			if (!isReplicationActive()) return;

			for (ReplicationReference ref : _dirtyRefs) {
				_dirtyRefs.remove(ref);

				final Object obj = ref.object();

				final ObjectReference exist = Util.getObjectReferenceById(getSession(), obj);
				if (exist == null) {
					ReplicationProviderSignature provider = getProviderSignature(ref.uuid().getSignaturePart());

					ObjectReference tmp = new ObjectReference();
					tmp.setClassName(obj.getClass().getName());
					tmp.setObjectId(Util.castAsLong(getSession().getIdentifier(obj)));

					Uuid uuid = new Uuid();
					uuid.setLongPart(ref.uuid().getLongPart());
					uuid.setProvider(provider);
					tmp.setUuid(uuid);

					tmp.setVersion(ref.version());

					getSession().save(tmp);
				} else {
					exist.setVersion(ref.version());
					getSession().update(exist);
				}
			}
		}
	}

	private final class MyObjectLifeCycleEventsListener extends ObjectLifeCycleEventsListenerImpl {
		public final void onPostInsert(PostInsertEvent event) {
			if (!isReplicationActive())
				super.onPostInsert(event);
		}

		protected final void ObjectUpdated(Object obj, Serializable id) {
			if (!isReplicationActive())
				super.ObjectUpdated(obj, id);
		}
	}
}
