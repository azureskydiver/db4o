package com.db4o.replication.hibernate.impl.ref_as_table;

import com.db4o.replication.hibernate.impl.AbstractObjectLifeCycleEventsListener;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.impl.UuidGenerator;
import com.db4o.replication.hibernate.metadata.ObjectReference;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PreDeleteEvent;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class RefAsTableObjectLifeCycleEventsListener extends AbstractObjectLifeCycleEventsListener {
// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ObjectLifeCycleEventsListener ---------------------

	public final void configure(Configuration cfg) {
		new RefAsTableTablesCreator(RefAsTableConfiguration.produce(cfg)).createTables();
		super.configure(cfg);
	}

	public void onPostInsert(PostInsertEvent event) {
		ensureAlive();

		Object entity = event.getEntity();

		if (Util.skip(entity)) return;

		long id = Shared.castAsLong(event.getId());

		ObjectReference ref = new ObjectReference();
		ref.setClassName(entity.getClass().getName());
		ref.setObjectId(id);

		ref.setUuid(UuidGenerator.next(getSession()));

		long ver = Util.getMaxVersion(getSession().connection()) + 1;
		ref.setVersion(ver);

		try {
			getSession().save(ref);
		} catch (Exception e) {
			System.out.println("ref = " + ref);
			throw new RuntimeException(e);
		}
	}

	protected void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();
		Connection con = session.connection();
		long newVer = Util.getMaxVersion(con) + 1;
		String sql = "UPDATE " + ObjectReference.TABLE_NAME
				+ " SET " + ObjectReference.VERSION + " = " + newVer
				+ " WHERE " + ObjectReference.CLASS_NAME + " = '" + obj.getClass().getName() + "'"
				+ " AND " + ObjectReference.OBJECT_ID + " = " + Shared.castAsLong(session.getIdentifier(obj));

		final Statement st = Util.getStatement(con);

		try {
			final int affected = st.executeUpdate(sql);

			if (affected != 1)
				throw new RuntimeException("unable to update the version of an object");
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closeStatement(st);
		}
	}

	protected final Uuid getUuid(Object entity) {
		return Shared.getUuid(getSession(), entity);
	}

	protected final void objectDeleted(PreDeleteEvent event) {
		super.objectDeleted(event);

		ObjectReference ref = Shared.getObjectReferenceById(getSession(), event.getEntity());
		if (ref == null) throw new RuntimeException("ObjectReference must exist");
		getSession().delete(ref);
	}
}
