package com.db4o.replication.hibernate;

import com.db4o.inside.replication.ReadonlyReplicationProviderSignature;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class UpdateEventListenerImpl extends EmptyInterceptor
		implements Interceptor, UpdateEventListener {
	private static final UpdateEventListener instance = new UpdateEventListenerImpl();
	private static final Map threadSessionMap = new HashMap();
	private static final Map sessionConfigurationMap = new HashMap();

	UpdateEventListenerImpl() {
		//empty
	}

	public static void configure(Configuration cfg) {
		new MetaDataTablesCreator(cfg).createTables();
		cfg.setInterceptor(instance);
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostUpdateEventListeners(new PostUpdateEventListener[]{instance});
	}

	public static void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);
		sessionConfigurationMap.put(session, cfg);
	}

	protected static void collectionUpdated(Object collection) {
		if (!(collection instanceof PersistentCollection))
			throw new RuntimeException(collection + " should always be PersistentCollection");

		PersistentCollection persistentCollection = ((PersistentCollection) collection);
		Object owner = persistentCollection.getOwner();

		if (skip(owner)) return;

		Serializable id = getId(owner);
		ObjectUpdated(owner, id);
	}

	protected static Serializable getId(Object obj) {
		Session session = getSession();
		return session.getIdentifier(obj);
	}

	protected static boolean skip(Object obj) {
		return obj instanceof ReplicationRecord || obj instanceof ReadonlyReplicationProviderSignature;
	}

	protected static Configuration getConfiguration() {
		return (Configuration) sessionConfigurationMap.get(getSession());
	}

	protected static Session getSession() {
		return (Session) threadSessionMap.get(Thread.currentThread());
	}

	protected static void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();
		if (session == null)
			throw new RuntimeException("Unable to update the version number of an object. Did you forget to call ReplicationConfigurator.install(session, cfg) after opening a session?");

		long newVersion = Util.getMaxVersion(session.connection()) + 1;
		Configuration cfg = getConfiguration();
		String tableName = Util.getTableName(cfg, obj.getClass());
		String primaryKeyColumnName = Util.getPrimaryKeyColumnName(cfg, obj);
		Connection connection = session.connection();
		Util.incrementObjectVersion(connection, id, newVersion, tableName, primaryKeyColumnName);
	}

	public void onPostUpdate(PostUpdateEvent event) {
		Object object = event.getEntity();

		if (skip(object)) return;

		ObjectUpdated(object, event.getId());
	}

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}
}