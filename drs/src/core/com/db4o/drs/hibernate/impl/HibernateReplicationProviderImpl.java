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
package com.db4o.drs.hibernate.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

import com.db4o.ObjectSet;
import com.db4o.drs.hibernate.metadata.ObjectReference;
import com.db4o.drs.hibernate.metadata.PeerSignature;
import com.db4o.drs.hibernate.metadata.ProviderSignature;
import com.db4o.drs.hibernate.metadata.Record;
import com.db4o.drs.hibernate.metadata.Uuid;
import com.db4o.drs.inside.CollectionHandler;
import com.db4o.drs.inside.CollectionHandlerImpl;
import com.db4o.drs.inside.ReadonlyReplicationProviderSignature;
import com.db4o.drs.inside.ReplicationReference;
import com.db4o.drs.inside.ReplicationReflector;
import com.db4o.ext.Db4oUUID;
import com.db4o.foundation.TimeStampIdGenerator;
import com.db4o.foundation.Visitor4;
import com.db4o.reflect.Reflector;


public final class HibernateReplicationProviderImpl implements HibernateReplicationProvider {
	private boolean _simpleObjectContainerCommitCalled = true;

	private Configuration _cfg;

	private final String _name;

	private Session _session;

	private SessionFactory _sessionFactory;

	private Transaction _transaction;

	private ObjectReferenceMap _objRefs = new ObjectReferenceMap();

	private boolean _alive = false;

	private ObjectLifeCycleEventsListener _listener = new MyObjectLifeCycleEventsListener();

	/**
	 * The Signature of the peer in the current Transaction.
	 */
	private PeerSignature _peerSignature;

	private FlushEventListener _flushEventListener = new MyFlushEventListener();

	/**
	 * The Record of {@link #_peerSignature}.
	 */
	private Record _replicationRecord;

	private final CollectionHandler _collectionHandler = new CollectionHandlerImpl();

	private Reflector _reflector = ReplicationReflector.getInstance().reflector();

	/**
	 * Objects which meta data not yet updated.
	 */
	private Set<ReplicationReference> _dirtyRefs = new HashSet();

	private boolean _inReplication = false;

	private final TimeStampIdGenerator _generator;

	public HibernateReplicationProviderImpl(Configuration cfg) {
		this(cfg, null);
	}

	public HibernateReplicationProviderImpl(Configuration cfg, String name) {
		_name = name;
		_cfg = ReplicationConfiguration.decorate(cfg);

		new TablesCreatorImpl(_cfg).validateOrCreate();

		_cfg.setInterceptor(EmptyInterceptor.INSTANCE);

		EventListeners el = _cfg.getEventListeners();
		el.setFlushEventListeners((FlushEventListener[])
				Util.add(el.getFlushEventListeners(), _flushEventListener));

		_listener.configure(cfg);

		_sessionFactory = getConfiguration().buildSessionFactory();
		_session = _sessionFactory.openSession();
		_session.setFlushMode(FlushMode.COMMIT);
		_transaction = _session.beginTransaction();

		_listener.install(getSession(), cfg);

		_generator = GeneratorMap.get(_session);

		_alive = true;
	}

	public final void activate(Object object) {
		Hibernate.initialize(object);
	}

	public final void clearAllReferences() {
		ensureReplicationActive();

		_objRefs.clear();
	}

	public final void commit() {
		final Session session = getSession();


		session.flush();
		_transaction.commit();
		clearSession();
		_transaction = session.beginTransaction();
		setCommitted(true);
	}

	public final synchronized void commitReplicationTransaction(long raisedDatabaseVersion) {
		ensureReplicationActive();

		getSession().flush();

		_generator.setMinimumNext(raisedDatabaseVersion);

		commit();
		_dirtyRefs.clear();

		_inReplication = false;
	}

	public final void delete(Object obj) {
		getSession().delete(obj);
		setCommitted(false);
	}

	public final void deleteAllInstances(Class clazz) {
		ensureReplicationInActive();
		List col = getSession().createCriteria(clazz).list();
		for (Object o : col)
			delete(o);
	}
	
	public final synchronized void destroy() {
		if (!_alive)
			throw new RuntimeException("Provider has already been destroyed.");
		
		_alive = false;

		_session.close();
		_sessionFactory.close();

		_transaction = null;
		_session = null;
		_sessionFactory = null;

		_dirtyRefs = null;

		_objRefs = null;

		_reflector = null;

		EventListeners eventListeners = getConfiguration().getEventListeners();
		FlushEventListener[] o1 = eventListeners.getFlushEventListeners();
		FlushEventListener[] r1 = (FlushEventListener[]) Util.removeElement(
				o1, _flushEventListener);
		if ((o1.length - r1.length) != 1)
			throw new RuntimeException("can't remove");

		eventListeners.setFlushEventListeners(r1);
		_flushEventListener = null;

		_listener.destroy();
		_listener = null;

		_cfg = null;
	}

	public final Configuration getConfiguration() {
		return _cfg;
	}

	public final long getCurrentVersion() {
		ensureReplicationActive();

		return _generator.generate();
	}
	
	public long getLastReplicationVersion() {
		ensureReplicationActive();

		return _replicationRecord.getTime();
	}

	public final Object getMonitor() {
		return this;
	}

	public final String getName() {
		return _name;
	}

	public final Session getSession() {
		return _session;
	}

	public final ReadonlyReplicationProviderSignature getSignature() {
		return Util.genMySignature(getSession());
	}

	public final ObjectSet getStoredObjects(Class aClass) {
		if (_collectionHandler.canHandle(aClass))
			throw new IllegalArgumentException("Hibernate does not query by Collection");

		getSession().flush();

		return new ObjectSetCollectionFacade(getSession().createCriteria(aClass).list());
	}

	public final ObjectSet objectsChangedSinceLastReplication() {
		ensureReplicationActive();

		getSession().flush();

		Set<PersistentClass> mappedClasses = new HashSet<PersistentClass>();

		Iterator classMappings = getConfiguration().getClassMappings();
		while (classMappings.hasNext()) {
			PersistentClass persistentClass = (PersistentClass) classMappings.next();
			Class claxx = persistentClass.getMappedClass();

			if (Util.isAssignableFromInternalObject(claxx))
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

	public final ReplicationReference produceReference(Object obj,
			Object referencingObj, String fieldName) {
		ensureReplicationActive();

		ReplicationReference existing = _objRefs.get(obj);
		if (existing != null)
			return existing;

		if (_collectionHandler.canHandle(obj))
			return null;
		else
			return produceObjectReference(obj);
	}

	public final ReplicationReference produceReferenceByUUID(final Db4oUUID uuid, Class hint) {
		ensureReplicationActive();

		if (uuid == null) throw new IllegalArgumentException("uuid cannot be null");
		if (hint == null) throw new IllegalArgumentException("hint cannot be null");

		getSession().flush();

		ReplicationReference exist = _objRefs.getByUUID(uuid);
		if (exist != null) return exist;

		if (_collectionHandler.canHandle(hint)) {
			return null;
		} else {
			return produceObjectReferenceByUUID(uuid);
		}
	}

	public final ReplicationReference referenceNewObject(Object obj,
			ReplicationReference counterpartReference,
			ReplicationReference referencingObjCounterPartRef, String fieldName) {
		ensureReplicationActive();

		if (obj == null)
			throw new NullPointerException("obj is null");
		if (counterpartReference == null)
			throw new NullPointerException("counterpartReference is null");

		if (_collectionHandler.canHandle(obj))
			return null;
		else {
			Db4oUUID uuid = counterpartReference.uuid();
			long version = counterpartReference.version();

			return _objRefs.put(obj, uuid, version);
		}
	}

	public void replicateDeletion(Db4oUUID uuid) {
		ObjectReference ref = Util.getByUUID(getSession(), translate(uuid));

		if (ref == null) return;

		Object loaded = getSession().get(ref.getClassName(), ref.getTypedId());
		if (loaded == null) return;

		getSession().delete(loaded);
	}

	public final synchronized void rollbackReplication() {
		ensureReplicationActive();

		_transaction.rollback();
		clearSession();

		_transaction = getSession().beginTransaction();
		clearAllReferences();
		_dirtyRefs.clear();
		_inReplication = false;
	}

	public final void startReplicationTransaction(ReadonlyReplicationProviderSignature aPeerSignature) {
		ensureReplicationInActive();
		ensureCommitted();

		_transaction.commit();
		_transaction = getSession().beginTransaction();
		clearSession();

		byte[] peerSigBytes = aPeerSignature.getSignature();

		if (Arrays.equals(peerSigBytes, getSignature().getSignature()))
			throw new RuntimeException("peerSigBytes must not equal to my own sig");

		final List exisitingSigs = getSession().createCriteria(PeerSignature.class)
				.add(Restrictions.eq(ProviderSignature.Fields.SIG, peerSigBytes)).list();

		if (exisitingSigs.size() == 1) {
			_peerSignature = (PeerSignature) exisitingSigs.get(0);
			List existingRecords = getSession()
							.createCriteria(Record.class)
							.createCriteria(Record.Fields.PEER_SIGNATURE)
							.add(Restrictions.eq(ProviderSignature.Fields.ID, _peerSignature.getId())).list();
			
			if (existingRecords.size()==1)	//This peer X replicated with this provider before 
				_replicationRecord = (Record) existingRecords.get(0);
			else if (existingRecords.size()==0)	//
				/*
				 * This peer X never replicated with this provider, 
				 * but objects from this peer X was replicated thru another provider, 
				 * .e.g. X->another provider->this provider
				 */
				createNewRecord();
			else
				throw new RuntimeException("This state is never reachable.");
		} else if (exisitingSigs.size() == 0) {
			_peerSignature = new PeerSignature(peerSigBytes);
			getSession().save(_peerSignature);
			getSession().flush();

			createNewRecord();
		} else
			throw new RuntimeException("result size = " + exisitingSigs.size() + ". It should be either 1 or 0");

		_generator.setMinimumNext(Util.getMaxReplicationRecordVersion(_session));

		_inReplication = true;
	}

	private void createNewRecord() {
		_replicationRecord = new Record();
		_replicationRecord.setPeerSignature(_peerSignature);
	}

	public final void storeNew(Object object) {
		ensureReplicationInActive();
		Session s = getSession();
		s.save(object);
		s.flush();
		setCommitted(false);
	}

	public final void storeReplica(Object entity) {
		ensureReplicationActive();

		getSession().flush();

		//Hibernate does not treat Collection as 1st class object, so storing a Collection is no-op
		if (_collectionHandler.canHandle(entity)) return;

		ReplicationReference ref = _objRefs.get(entity);
		if (ref == null) throw new RuntimeException("Reference should always be available before storeReplica");


		final Session s = getSession();
		if (s.contains(entity)) {
			s.update(entity);
		} else {
			s.save(entity);
		}

		_dirtyRefs.add(ref);

		getSession().flush();
	}

	public boolean supportsCascadeDelete() {
		return true;
	}

	public boolean supportsHybridCollection() {
		return false;
	}

	public boolean supportsMultiDimensionalArrays() {
		return false;
	}

	public boolean supportsRollback() {
		return true;
	}

	public final void syncVersionWithPeer(long version) {
		ensureReplicationActive();

		_replicationRecord.setTime(version);
		getSession().saveOrUpdate(_replicationRecord);
		getSession().flush();
	}

	public final String toString() {
		return _name;
	}

	public final void update(Object obj) {
		ensureReplicationInActive();
		if (!_collectionHandler.canHandle(obj)) {
			getSession().flush();
			getSession().update(obj);
			getSession().flush();
		}
		setCommitted(false);
	}

	public void updateCounterpart(Object entity) {
		ensureReplicationActive();

		getSession().flush();

		//Hibernate does not treat Collection as 1st class object, so storing a Collection is no-op
		if (_collectionHandler.canHandle(entity)) return;

		ReplicationReference ref = _objRefs.get(entity);
		if (ref == null) throw new RuntimeException("Reference should always be available before storeReplica");


		getSession().update(entity);

		_dirtyRefs.add(ref);

		getSession().flush();
	}

	public final void visitCachedReferences(Visitor4 visitor) {
		ensureReplicationActive();

		_objRefs.visitEntries(visitor);
	}

	public final boolean wasModifiedSinceLastReplication(ReplicationReference reference) {
		ensureReplicationActive();
		return reference.version() > getLastReplicationVersion();
	}

	Uuid translate(Db4oUUID du) {
		Uuid uuid = new Uuid();
		uuid.setCreated(du.getLongPart());
		uuid.setProvider(produceProviderSignature(du.getSignaturePart()));
		return uuid;
	}

	private void clearSession() {
		getSession().clear();
	}

	private void ensureAlive() {
		if (!_alive)
			throw new UnsupportedOperationException("This provider is dead because #destroy() is called");
	}

	private void ensureCommitted() {
		if (!_simpleObjectContainerCommitCalled)
			throw new RuntimeException("Please call commit() first");
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
		long lastReplicationVersion = getLastReplicationVersion();
		criteria.add(Restrictions.gt(ObjectReference.Fields.VERSION, lastReplicationVersion));
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
			disjunction.add(Restrictions.eq(ObjectReference.Fields.CLASS_NAME, s));

		criteria.add(disjunction);

		Set out = new HashSet();
		for (Object o : criteria.list()) {
			ObjectReference ref = (ObjectReference) o;
			out.add(getSession().load(persistentClass.getRootClass().getClassName(), ref.getTypedId()));
		}
		return out;
	}

	/**
	 * Get the ProviderSignature of the given signaturePart.
	 * If not found, generate a ProviderSignature for the given signaturePart on the fly.
	 * @param signaturePart
	 * @return
	 */
	private ProviderSignature produceProviderSignature(byte[] signaturePart) {
		final List exisitingSigs = getSession().createCriteria(ProviderSignature.class)
				.add(Restrictions.eq(ProviderSignature.Fields.SIG, signaturePart))
				.list();
		if (exisitingSigs.size() == 1) 
		{
			return (ProviderSignature) exisitingSigs.get(0);
		}
		else if (exisitingSigs.size() == 0) 
		{
			ProviderSignature alienProvider = new PeerSignature(signaturePart);
			getSession().save(alienProvider);
			getSession().flush();
			return alienProvider;
		} 
		else
		{
			throw new RuntimeException("This condition should never be reachable.It is impossible to have more than two records for one sig.");
		}
	}

	private boolean isReplicationActive() {
		return _inReplication;
	}

	private ReplicationReference produceObjectReference(Object obj) {
		if (!getSession().contains(obj)) return null;
		long id = Util.castAsLong(getSession().getIdentifier(obj));
		final ObjectReference ref = Util.getObjectReferenceById(getSession(), obj.getClass().getName(), id);

		if (ref == null) throw new RuntimeException("ObjectReference must exist for " + obj);

		Uuid uuid = ref.getUuid();
		return _objRefs.put(obj, new Db4oUUID(uuid.getCreated(), uuid.getProvider().getSignature()), ref.getModified());
	}

	private ReplicationReference produceObjectReferenceByUUID(Db4oUUID uuid) {
		ObjectReference of = Util.getByUUID(getSession(), translate(uuid));
		if (of == null)
			return null;
		else {
			Object obj = getSession().load(of.getClassName(), of.getTypedId());

			return _objRefs.put(obj, uuid, of.getModified());
		}
	}

	private void setCommitted(boolean b) {
		_simpleObjectContainerCommitCalled = b;
	}

	private final class MyFlushEventListener implements FlushEventListener {
		public final void onFlush(FlushEvent event) throws HibernateException {
			if (!isReplicationActive()) return;

			for (ReplicationReference ref : _dirtyRefs) {
				_dirtyRefs.remove(ref);

				final Object obj = ref.object();
				long id = Util.castAsLong(getSession().getIdentifier(obj));

				Uuid uuid = translate(ref.uuid());

				final ObjectReference exist = Util.getByUUID(getSession(), uuid);
				if (exist == null) {
					ObjectReference tmp = new ObjectReference();
					tmp.setClassName(obj.getClass().getName());
					tmp.setTypedId(Util.castAsLong(getSession().getIdentifier(obj)));
					tmp.setUuid(uuid);
					tmp.setModified(ref.version());
					try {
						getSession().save(tmp);
					} catch (HibernateException e) {
						throw new RuntimeException(e);
					}
				} else {
					if (!exist.getClassName().equals(obj.getClass().getName()))
						throw new RuntimeException("Same classname expected");

					if (exist.getTypedId() != id) //deletion rollback case, id may change
						exist.setTypedId(id);

					exist.setModified(ref.version());
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
				super.ObjectUpdated(obj, Util.castAsLong(id));
		}
	}

	public boolean isProviderSpecific(Object original) {
		return original.getClass().getName().startsWith("org.hibernate.collection.");
	}
}
