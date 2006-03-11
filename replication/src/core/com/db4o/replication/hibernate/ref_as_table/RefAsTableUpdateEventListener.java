package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.AbstractUpdateEventListener;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostUpdateEventListener;

import java.io.Serializable;

public class RefAsTableUpdateEventListener extends AbstractUpdateEventListener {
	public void configure(Configuration cfg) {
		new RefAsTableTablesCreator(RefAsTableConfiguration.produce(cfg)).execute();
		cfg.setInterceptor(this);
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostUpdateEventListeners(new PostUpdateEventListener[]{this});
	}

	protected void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();
		Shared.incrementObjectVersion(session, obj, Shared.castAsLong(id));
	}
}
