package com.db4o.replication.hibernate.impl.ref_as_table;

import com.db4o.replication.hibernate.impl.AbstractObjectLifeCycleEventsListener;
import com.db4o.replication.hibernate.impl.Constants;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.impl.UuidGenerator;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostInsertEvent;

import java.io.Serializable;

public class RefAsTableObjectLifeCycleEventsListener extends AbstractObjectLifeCycleEventsListener {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ObjectLifeCycleEventsListener ---------------------


	public void configure(Configuration cfg) {
		new RefAsTableTablesCreator(RefAsTableConfiguration.produce(cfg)).createTables();
		Util.initMySignature(cfg);

		setListeners(cfg);
	}

	public void onPostInsert(PostInsertEvent event) {
		Object entity = event.getEntity();

		if (Util.skip(entity)) return;

		long id = Shared.castAsLong(event.getId());

		ObjectReference ref = new ObjectReference();
		ref.setClassName(entity.getClass().getName());
		ref.setObjectId(id);

		UuidGenerator uuidGenerator = sessionUuidGeneratorMap.get(getSession());

		ref.setUuid(uuidGenerator.next());

		ref.setVersion(Constants.MIN_VERSION_NO);
		getSession().save(ref);
	}

	protected Uuid getUuid(Object entity) {
		return Shared.getUuid(getSession(), entity);
	}

	protected void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();
		Shared.incrementObjectVersion(session, obj, Shared.castAsLong(id));
	}
}
