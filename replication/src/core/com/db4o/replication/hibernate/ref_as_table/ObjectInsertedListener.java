package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.common.Common;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

public class ObjectInsertedListener implements PostInsertEventListener {
	public void onPostInsert(PostInsertEvent event) {
		//TODO
		Object entity = event.getEntity();

		if (Common.skip(entity)) return;

		long id = Shared.castAsLong(event.getId());

		ReplicationReference ref = new ReplicationReference();
		ref.setClassName(entity.getClass().getName());
		ref.setObjectId(id);

		Session s = event.getPersister().getFactory().getCurrentSession();
		Transaction tx = s.beginTransaction();
		s.save(ref);

		tx.commit();
	}
}
