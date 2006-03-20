package com.db4o.replication.hibernate;

import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

import java.io.Serializable;

public interface UpdateEventListener extends Interceptor, PostUpdateEventListener {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface Interceptor ---------------------


	public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException;

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException;

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException;

// --------------------- Interface PostUpdateEventListener ---------------------

	public void onPostUpdate(PostUpdateEvent event);

	void configure(Configuration cfg);

	void install(Session session, Configuration cfg);
}
