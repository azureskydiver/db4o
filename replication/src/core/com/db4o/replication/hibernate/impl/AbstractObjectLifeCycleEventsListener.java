package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.ObjectLifeCycleEventsListener;
import com.db4o.replication.hibernate.metadata.DeletedObject;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractObjectLifeCycleEventsListener extends EmptyInterceptor
		implements ObjectLifeCycleEventsListener {
// ------------------------------ FIELDS ------------------------------

	protected final Map<Thread, Session> threadSessionMap = new HashMap();
	protected final Map<Session, UuidGenerator> sessionUuidGeneratorMap = new HashMap();

	//protected UuidGenerator uuidGenerator = new UuidGenerator();

// --------------------------- CONSTRUCTORS ---------------------------

	protected AbstractObjectLifeCycleEventsListener() {
		//empty
	}

// --------------------- GETTER / SETTER METHODS ---------------------

	protected final Session getSession() {
		Session session = threadSessionMap.get(Thread.currentThread());

		if (session == null)
			throw new RuntimeException("Unable to update the version number of an object. Did you forget to call ReplicationConfigurator.install(session, cfg) after opening a session?");
		return session;
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ObjectLifeCycleEventsListener ---------------------

	public void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);

		UuidGenerator tmp = new UuidGenerator();
		tmp.reset(session);
		sessionUuidGeneratorMap.put(session, tmp);
	}

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}

	public void onPostUpdate(PostUpdateEvent event) {
		Object object = event.getEntity();

		if (Util.skip(object)) return;

		ObjectUpdated(object, event.getId());
	}

	public boolean onPreDelete(PreDeleteEvent event) {
		boolean veto = false;

		deleteReplicationComponentIdentity(event);

		Uuid uuid = getUuid(event.getEntity());
		DeletedObject deletedObject = new DeletedObject();
		deletedObject.setUuid(uuid);
		getSession().save(deletedObject);

		return veto;
	}

	private void deleteReplicationComponentIdentity(PreDeleteEvent event) {
		Session s = getSession();
		Uuid uuid = getUuid(event.getEntity());
		ReplicationComponentIdentity rci = Util.getReplicationComponentIdentityByRefObjUuid(s, uuid);

		if (rci != null) s.delete(rci);
	}

	protected abstract Uuid getUuid(Object entity);

// -------------------------- OTHER METHODS --------------------------

	protected abstract void ObjectUpdated(Object obj, Serializable id);

	protected final void collectionUpdated(Object collection) {
		if (!(collection instanceof PersistentCollection))
			throw new RuntimeException(collection + " should always be PersistentCollection");

		PersistentCollection persistentCollection = ((PersistentCollection) collection);
		Object owner = persistentCollection.getOwner();

		if (Util.skip(owner)) return;

		Serializable id = getId(owner);
		ObjectUpdated(owner, id);
	}

	protected final Serializable getId(Object obj) {
		Session session = getSession();
		return session.getIdentifier(obj);
	}

	protected void setListeners(Configuration cfg) {
		cfg.setInterceptor(this);
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostUpdateEventListeners(new PostUpdateEventListener[]{this});
		eventListeners.setPostInsertEventListeners(new PostInsertEventListener[]{this});
		eventListeners.setPreDeleteEventListeners(new PreDeleteEventListener[]{this});
	}
}
