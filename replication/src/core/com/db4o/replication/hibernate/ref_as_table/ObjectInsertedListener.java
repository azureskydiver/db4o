package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.common.Common;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

import java.util.HashMap;
import java.util.Map;

public class ObjectInsertedListener implements PostInsertEventListener {
	protected final Map<Thread, Session> threadSessionMap = new HashMap();

	public ObjectInsertedListener() {
	}

	public void configure(Configuration cfg) {
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostInsertEventListeners(new PostInsertEventListener[]{this});
	}

	public void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);
	}

	public void onPostInsert(PostInsertEvent event) {
		insertRef(event);
	}

	private void insertRef(PostInsertEvent event) {
		Object entity = event.getEntity();

		if (Common.skip(entity)) return;

		long id = Shared.castAsLong(event.getId());

		ReplicationReference ref = new ReplicationReference();
		ref.setClassName(entity.getClass().getName());
		ref.setObjectId(id);
		ref.setVersion(Common.MIN_VERSION_NO);
		getSession().save(ref);
	}

	protected final Session getSession() {
		Session session = threadSessionMap.get(Thread.currentThread());

		if (session == null)
			throw new RuntimeException("Unable to insert the replication reference of an object. Did you forget to call ReplicationConfigurator.refAsTableInstall(session, cfg) after opening a session?");
		return session;
	}
}
