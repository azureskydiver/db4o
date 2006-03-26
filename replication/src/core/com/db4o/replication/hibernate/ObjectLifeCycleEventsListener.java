package com.db4o.replication.hibernate;

import org.hibernate.CallbackException;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.FlushEvent;
import org.hibernate.event.FlushEventListener;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEvent;
import org.hibernate.event.PreDeleteEventListener;

import java.io.Serializable;

public interface ObjectLifeCycleEventsListener extends
		PostInsertEventListener, PostUpdateEventListener,
		PreDeleteEventListener, Interceptor, FlushEventListener {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface FlushEventListener ---------------------

	void onFlush(FlushEvent event) throws HibernateException;

// --------------------- Interface Interceptor ---------------------

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException;

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException;

// --------------------- Interface PostInsertEventListener ---------------------

	void onPostInsert(PostInsertEvent event);

// --------------------- Interface PostUpdateEventListener ---------------------


	public void onPostUpdate(PostUpdateEvent event);

// --------------------- Interface PreDeleteEventListener ---------------------

	boolean onPreDelete(PreDeleteEvent event);

	void configure(Configuration cfg);

	void destroy();

	void install(Session session, Configuration cfg);
}
