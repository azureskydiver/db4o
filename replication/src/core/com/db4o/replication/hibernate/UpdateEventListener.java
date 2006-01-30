package com.db4o.replication.hibernate;

import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

import java.io.Serializable;

public interface UpdateEventListener extends Interceptor, PostUpdateEventListener {
	public void onPostUpdate(PostUpdateEvent event);

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException;

	public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException;

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException;
}
