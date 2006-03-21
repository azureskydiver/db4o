package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.impl.AbstractObjectLifeCycleEventsListener;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.impl.UuidGenerator;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostInsertEvent;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RefAsColumnsObjectLifeCycleEventsListener extends AbstractObjectLifeCycleEventsListener {
// ------------------------------ FIELDS ------------------------------

	private final Map<Session, ObjectConfig> sessionConfigurationMap = new HashMap();

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface ObjectLifeCycleEventsListener ---------------------

	public final void configure(Configuration cfg) {
		new RefAsColumnsTablesCreator(RefAsColumnsConfiguration.produce(cfg)).createTables();
		super.configure(cfg);
	}

	public final void install(Session session, Configuration cfg) {
		super.install(session, cfg);
		sessionConfigurationMap.put(session, new ObjectConfig(cfg));
	}

	public void onPostInsert(PostInsertEvent event) {
		ensureAlive();

		Object entity = event.getEntity();
		if (Util.skip(entity)) return;

		Serializable id = event.getId();
		ObjectConfig objectConfig = getObjectConfig();
		String tName = objectConfig.getTableName(entity.getClass());
		String pName = objectConfig.getPrimaryKeyColumnName(entity);

		long ver = Util.getMaxVersion(getSession().connection()) + 1;
		Uuid uuid = UuidGenerator.next(getSession());
		Shared.updateMetadata(ver, uuid.getLongPart(), id, tName, pName, getSession(), uuid.getProvider().getId());
	}

	protected void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();

		long newVersion = Util.getMaxVersion(session.connection()) + 1;
		ObjectConfig objectConfig = getObjectConfig();

		String tableName = objectConfig.getTableName(obj.getClass());
		String primaryKeyColumnName = objectConfig.getPrimaryKeyColumnName(obj);
		Connection connection = session.connection();
		PreparedStatement ps = null;

		try {
			String sql = "UPDATE " + tableName + " SET " + Db4oColumns.VERSION.name + "=?"
					+ " WHERE " + primaryKeyColumnName + " =?";
			ps = connection.prepareStatement(sql);
			ps.setLong(1, newVersion);
			ps.setObject(2, id);

			int affected = ps.executeUpdate();
			if (affected != 1) {
				throw new RuntimeException("Unable to update the version column");
			}
			ps.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			Util.closePreparedStatement(ps);
		}
	}

	protected final Uuid getUuid(Object entity) {
		ObjectConfig objectConfig = getObjectConfig();
		String tName = objectConfig.getTableName(entity.getClass());
		String pName = objectConfig.getPrimaryKeyColumnName(entity);
		Serializable identifier = getSession().getIdentifier(entity);

		return (Uuid) Shared.getUuidAndVersion(tName, pName, identifier, getSession())[0];
	}

	private ObjectConfig getObjectConfig() {
		return sessionConfigurationMap.get(getSession());
	}
}