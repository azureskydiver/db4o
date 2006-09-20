package com.db4o.drs.hibernate.impl;

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

/**
 * @author Albert Kwan
 */
public interface ObjectLifeCycleEventsListener extends
		PostInsertEventListener, PostUpdateEventListener,
		PreDeleteEventListener, Interceptor, FlushEventListener {
	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException;

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException;

	public void onPostUpdate(PostUpdateEvent event);

	void configure(Configuration cfg);

	void destroy();

	void install(Session session, Configuration cfg);

	void onFlush(FlushEvent event) throws HibernateException;

	void onPostInsert(PostInsertEvent event);

	boolean onPreDelete(PreDeleteEvent event);
}
