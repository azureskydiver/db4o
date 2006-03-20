package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.UpdateEventListener;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.event.PostUpdateEvent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUpdateEventListener extends EmptyInterceptor
		implements UpdateEventListener {
// ------------------------------ FIELDS ------------------------------

	protected final Map<Thread, Session> threadSessionMap = new HashMap();

// --------------------------- CONSTRUCTORS ---------------------------

	protected AbstractUpdateEventListener() {
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

// --------------------- Interface UpdateEventListener ---------------------


	public void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);
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
}
