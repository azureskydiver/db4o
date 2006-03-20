package com.db4o.replication.hibernate.impl.ref_as_table;

import com.db4o.replication.hibernate.ObjectInsertedListener;
import com.db4o.replication.hibernate.impl.Constants;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

import java.util.HashMap;
import java.util.Map;

public class ObjectInsertedListenerImpl implements ObjectInsertedListener {
// ------------------------------ FIELDS ------------------------------

	protected final Map<Thread, Session> threadSessionMap = new HashMap();

// --------------------------- CONSTRUCTORS ---------------------------

	public ObjectInsertedListenerImpl() {
	}

// --------------------- GETTER / SETTER METHODS ---------------------

	protected final Session getSession() {
		Session session = threadSessionMap.get(Thread.currentThread());

		if (session == null)
			throw new RuntimeException("Unable to insert the replication reference of an object. Did you forget to call ReplicationConfigurator.refAsTableInstall(session, cfg) after opening a session?");
		return session;
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ObjectInsertedListener ---------------------

	public void configure(Configuration cfg) {
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostInsertEventListeners(new PostInsertEventListener[]{this});
	}

	public void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);
	}

// --------------------- Interface PostInsertEventListener ---------------------


	public void onPostInsert(PostInsertEvent event) {
		insertRef(event);
	}

	private void insertRef(PostInsertEvent event) {
		Object entity = event.getEntity();

		if (Util.skip(entity)) return;

		long id = Shared.castAsLong(event.getId());

		ObjectReference ref = new ObjectReference();
		ref.setClassName(entity.getClass().getName());
		ref.setObjectId(id);
		ref.setVersion(Constants.MIN_VERSION_NO);
		getSession().save(ref);
	}
}
