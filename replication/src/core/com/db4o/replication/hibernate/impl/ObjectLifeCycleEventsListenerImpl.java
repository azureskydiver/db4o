package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.ObjectLifeCycleEventsListener;
import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.CallbackException;
import org.hibernate.Criteria;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.PersistentCollection;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObjectLifeCycleEventsListenerImpl extends EmptyInterceptor
		implements ObjectLifeCycleEventsListener {
// ------------------------------ FIELDS ------------------------------

	public Set<ObjectReference> dirtyNewRefs = new HashSet();
	public Set<ObjectReference> dirtyUpdatedRefs = new HashSet();
	private Set<Configuration> configs = new HashSet<Configuration>();
	private Map<Thread, Session> threadSessionMap = new HashMap();
	private boolean _alive = true;

// --------------------- GETTER / SETTER METHODS ---------------------

	private Session getSession() {
		Session session = threadSessionMap.get(Thread.currentThread());

		if (session == null)
			throw new RuntimeException("Unable to update the version number of an object. Did you forget to call ReplicationConfigurator.install(session, cfg) after opening a session?");
		return session;
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ObjectLifeCycleEventsListener ---------------------

	public final void configure(Configuration cfg) {
		new TablesCreatorImpl(ReplicationConfiguration.decorate(cfg)).createTables();

		Util.initMySignature(cfg);
		Util.initUuidLongPartSequence(cfg);

		configs.add(cfg);
		setListeners(cfg);
	}

	public final void destroy() {
		_alive = false;
		threadSessionMap.clear();
		threadSessionMap = null;

		for (Configuration cfg : configs)
			resetListeners(cfg);

		configs.clear();
		configs = null;
	}

	public final void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);
	}

	public final void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}

	public final void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}

	public void onFlush(FlushEvent event) throws HibernateException {
		final Session s = getSession();
		final long ver = Util.getMaxVersion(s.connection()) + 1;

		for (ObjectReference ref : dirtyNewRefs) {
			ref.setUuid(UuidGenerator.next(s));
			ref.setVersion(ver);
			getSession().save(ref);
		}

		dirtyNewRefs.clear();

		for (ObjectReference refT : dirtyUpdatedRefs) {
			final ObjectReference persisted = Util.getObjectReferenceById(getSession(), refT.getClassName(), refT.getObjectId());
			if (persisted == null) {
				//do nothing
			} else {
				persisted.setVersion(ver);
				getSession().update(persisted);
			}
		}

		dirtyUpdatedRefs.clear();
	}

	public void onPostInsert(PostInsertEvent event) {
		ensureAlive();

		Object entity = event.getEntity();

		if (Util.skip(entity)) return;
		long id = Util.castAsLong(event.getId());

		ObjectReference ref = new ObjectReference();

		ref.setClassName(entity.getClass().getName());
		ref.setObjectId(id);

		dirtyNewRefs.add(ref);
	}

	public final void onPostUpdate(PostUpdateEvent event) {
		ensureAlive();
		Object object = event.getEntity();

		if (Util.skip(object)) return;

		ObjectUpdated(object, Util.castAsLong(event.getId()));
	}

	public final boolean onPreDelete(PreDeleteEvent event) {
		ensureAlive();

		boolean veto = false;

		objectDeleted(event);

		return veto;
	}

	void ObjectUpdated(Object obj, long id) {
		final Session session = getSession();

		ObjectReference ref = new ObjectReference();
		ref.setObjectId(id);
		ref.setClassName(obj.getClass().getName());

		dirtyUpdatedRefs.add(ref);

//		Connection con = session.connection();
//		long newVer = Util.getMaxVersion(con) + 1;
//			Util.dumpTable("that",getSession(), "ObjectReference");

//		final ObjectReference ref = Util.getObjectReferenceById(session, obj, id);
//		if (ref == null) {
//			Util.dumpTable("that",getSession(), "ObjectReference");
//			System.out.println("obj = " + obj);
//			System.out.println("id = " + id);
//			throw new RuntimeException("ObjectReference does not exist");
//		}

//		String sql = "UPDATE " + ObjectReference.TABLE_NAME
//				+ " SET " + ObjectReference.VERSION + " = " + newVer
//				+ " WHERE " + ObjectReference.CLASS_NAME + " = '" + obj.getClass().getName() + "'"
//				+ " AND " + ObjectReference.OBJECT_ID + " = " + Util.castAsLong(session.getIdentifier(obj));
//
//		final Statement st = Util.getStatement(con);
//
//		try {
//			final int affected = st.executeUpdate(sql);
//
//			if (affected != 1)
//				throw new RuntimeException("unable to update the version of an object");
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		} finally {
//			Util.closeStatement(st);
//		}
	}

	private void collectionUpdated(Object collection) {
		if (!(collection instanceof PersistentCollection))
			throw new RuntimeException(collection + " should always be PersistentCollection");

		PersistentCollection persistentCollection = ((PersistentCollection) collection);
		Object owner = persistentCollection.getOwner();

		if (Util.skip(owner)) return;

		ObjectUpdated(owner, getId(owner));
	}

	private void deleteReplicationComponentIdentity(PreDeleteEvent event) {
		Session s = getSession();
		Uuid uuid = getUuid(event.getEntity());

		Criteria criteria = s.createCriteria(ReplicationComponentIdentity.class);
		criteria.add(Restrictions.eq("referencingObjectUuidLongPart", uuid.getLongPart()));
		criteria.createCriteria("provider").add(Restrictions.eq("bytes", uuid.getProvider().getBytes()));

		final List exisitings = criteria.list();
		int count = exisitings.size();

		if (count == 0) {
			//do nothing
		} else if (count > 1)
			throw new RuntimeException("Duplicated ReplicationComponentIdentity");
		else
			s.delete(exisitings.get(0));
	}

	private void ensureAlive() {if (!_alive) throw new RuntimeException("dead");}

	private long getId(Object obj) {
		Session session = getSession();
		return (Long) session.getIdentifier(obj);
	}

	private Uuid getUuid(Object entity) {
		return Util.getUuid(getSession(), entity);
	}

	private void objectDeleted(PreDeleteEvent event) {
		//deleteReplicationComponentIdentity(event);
		ObjectReference ref = Util.getObjectReferenceById(getSession(), event.getEntity().getClass().getName(), Util.castAsLong(event.getId()));
		ref.setDeleted(true);
		getSession().update(ref);
	}

	private void resetListeners(Configuration cfg) {
		cfg.setInterceptor(EmptyInterceptor.INSTANCE);
		EventListeners el = cfg.getEventListeners();

		PostUpdateEventListener[] o2 = el.getPostUpdateEventListeners();
		PostUpdateEventListener[] r2 = (PostUpdateEventListener[])
				ArrayUtils.removeElement(o2, this);
		if ((o2.length - r2.length) != 1)
			throw new RuntimeException("can't remove");
		el.setPostUpdateEventListeners(r2);

		PreDeleteEventListener[] o3 = el.getPreDeleteEventListeners();
		PreDeleteEventListener[] r3 = (PreDeleteEventListener[])
				ArrayUtils.removeElement(o3, this);
		if ((o3.length - r3.length) != 1)
			throw new RuntimeException("can't remove");
		el.setPreDeleteEventListeners(r3);

		PostInsertEventListener[] o4 = el.getPostInsertEventListeners();
		PostInsertEventListener[] r4 = (PostInsertEventListener[])
				ArrayUtils.removeElement(o4, this);
		if ((o4.length - r4.length) != 1)
			throw new RuntimeException("can't remove");

		FlushEventListener[] o5 = el.getFlushEventListeners();
		FlushEventListener[] r5 = (FlushEventListener[])
				ArrayUtils.removeElement(o5, this);
		if ((o5.length - r5.length) != 1)
			throw new RuntimeException("can't remove");

		el.setPostInsertEventListeners(r4);
	}

	private void setListeners(Configuration cfg) {
		cfg.setInterceptor(this);

		EventListeners el = cfg.getEventListeners();

		el.setPostUpdateEventListeners((PostUpdateEventListener[])
				ArrayUtils.add(el.getPostUpdateEventListeners(), this));

		el.setPostInsertEventListeners((PostInsertEventListener[])
				ArrayUtils.add(el.getPostInsertEventListeners(), this));

		el.setPreDeleteEventListeners((PreDeleteEventListener[])
				ArrayUtils.add(el.getPreDeleteEventListeners(), this));

		el.setFlushEventListeners((FlushEventListener[])
				ArrayUtils.add(el.getFlushEventListeners(), this));
	}
}
