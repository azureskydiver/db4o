package com.db4o.replication.hibernate;

import com.db4o.replication.hibernate.common.Common;
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
	protected final Map<Thread, Session> threadSessionMap = new HashMap();
	protected final Map<Session, Configuration> sessionConfigurationMap = new HashMap();

	protected AbstractUpdateEventListener() {
		//empty
	}

	public void onPostUpdate(PostUpdateEvent event) {
		Object object = event.getEntity();

		if (Common.skip(object)) return;

		ObjectUpdated(object, event.getId());
	}

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}

	protected abstract void ObjectUpdated(Object obj, Serializable id);

	protected final Serializable getId(Object obj) {
		Session session = getSession();
		return session.getIdentifier(obj);
	}

	protected final Configuration getConfiguration() {
		return sessionConfigurationMap.get(getSession());
	}

	protected final Session getSession() {
		return threadSessionMap.get(Thread.currentThread());
	}

	protected final void collectionUpdated(Object collection) {
		if (!(collection instanceof PersistentCollection))
			throw new RuntimeException(collection + " should always be PersistentCollection");

		PersistentCollection persistentCollection = ((PersistentCollection) collection);
		Object owner = persistentCollection.getOwner();

		if (Common.skip(owner)) return;

		Serializable id = getId(owner);
		ObjectUpdated(owner, id);
	}
}
