package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.ObjectDeletedListener;
import com.db4o.replication.hibernate.impl.ref_as_table.Shared;
import com.db4o.replication.hibernate.metadata.DeletedObject;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostDeleteEventListener;

import java.util.HashMap;
import java.util.Map;

public abstract class ObjectDeletedListenerImpl implements ObjectDeletedListener {
// ------------------------------ FIELDS ------------------------------

	protected final Map<Thread, Session> threadSessionMap = new HashMap();

// --------------------------- CONSTRUCTORS ---------------------------

	public ObjectDeletedListenerImpl() {
	}

// --------------------- GETTER / SETTER METHODS ---------------------

	protected final Session getSession() {
		Session session = threadSessionMap.get(Thread.currentThread());

		if (session == null)
			throw new RuntimeException("Unable to delete the replication reference of an object. Did you forget to call ReplicationConfigurator.refAsTableInstall(session, cfg) after opening a session?");
		return session;
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ObjectDeletedListener ---------------------

	public void configure(Configuration cfg) {
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostDeleteEventListeners(new PostDeleteEventListener[]{this});
	}

	public void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);
	}

// --------------------- Interface PostDeleteEventListener ---------------------


	public void onPostDelete(PostDeleteEvent event) {
		insertDeleteRecord(event);
	}

// -------------------------- OTHER METHODS --------------------------

	protected abstract Uuid getUuid(PostDeleteEvent event);

	private void insertDeleteRecord(PostDeleteEvent event) {
		Object entity = event.getEntity();

		if (Util.skip(entity)) return;

		if (getUuid(event) == null) return;

		DeletedObject record = new DeletedObject();


		long id = Shared.castAsLong(event.getId());

		ObjectReference ref = new ObjectReference();
		ref.setClassName(entity.getClass().getName());
		//ref.setHibernateObjectId(id);
		ref.setVersion(Constants.MIN_VERSION_NO);
		getSession().save(ref);
	}
}
