package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.common.Common;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

public class ObjectInsertedListener implements PostInsertEventListener {
	public ObjectInsertedListener() {
	}

	public void configure(Configuration cfg) {
		Common.setCurrentSessionContext(cfg);
	}

	public void onPostInsert(PostInsertEvent event) {
		Object entity = event.getEntity();

		if (Common.skip(entity)) return;

		long id = Shared.castAsLong(event.getId());

		ReplicationReference ref = new ReplicationReference();
		ref.setClassName(entity.getClass().getName());
		ref.setObjectId(id);

		Session s = event.getPersister().getFactory().getCurrentSession();
		s.save(ref);
	}
}
