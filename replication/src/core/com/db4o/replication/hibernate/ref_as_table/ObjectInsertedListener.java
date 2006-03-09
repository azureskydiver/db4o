package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.common.Common;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

import java.io.Serializable;

public class ObjectInsertedListener implements PostInsertEventListener {
	public void onPostInsert(PostInsertEvent event) {
		Object entity = event.getEntity();

		if (Common.skip(entity)) return;

		Serializable id = event.getId();

	}
}
