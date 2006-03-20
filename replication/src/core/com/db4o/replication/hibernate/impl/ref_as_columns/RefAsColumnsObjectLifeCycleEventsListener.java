package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.impl.AbstractObjectLifeCycleEventsListener;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.Uuid;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.PostInsertEvent;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class RefAsColumnsObjectLifeCycleEventsListener extends AbstractObjectLifeCycleEventsListener {
// ------------------------------ FIELDS ------------------------------

	protected final Map<Session, Configuration> sessionConfigurationMap = new HashMap();

// --------------------------- CONSTRUCTORS ---------------------------

	public RefAsColumnsObjectLifeCycleEventsListener() {
		//empty
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface UpdateEventListener ---------------------


	public void onPostInsert(PostInsertEvent event) {

	}

	public void configure(Configuration cfg) {
		new RefAsColumnsTablesCreator(RefAsColumnsConfiguration.produce(cfg)).createTables();
		Util.initMySignature(cfg);

		setListeners(cfg);
	}

	public void install(Session session, Configuration cfg) {
		super.install(session, cfg);
		sessionConfigurationMap.put(session, cfg);
	}

	protected Uuid getUuid(Object entity) {
		ObjectConfig objectConfig = new ObjectConfig(getConfiguration());
		String tName = objectConfig.getTableName(entity.getClass());
		String pName = objectConfig.getPrimaryKeyColumnName(entity);
		Serializable identifier = getSession().getIdentifier(entity);

		return (Uuid) Shared.getUuidAndVersion(tName, pName, identifier, getSession())[0];
	}

	protected void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();

		long newVersion = Util.getMaxVersion(session.connection()) + 1;
		Configuration cfg = getConfiguration();

		ObjectConfig objectConfig = new ObjectConfig(cfg);

		String tableName = objectConfig.getTableName(obj.getClass());
		String primaryKeyColumnName = objectConfig.getPrimaryKeyColumnName(obj);
		Connection connection = session.connection();
		Shared.incrementObjectVersion(connection, id, newVersion, tableName, primaryKeyColumnName);
	}

	protected final Configuration getConfiguration() {
		return sessionConfigurationMap.get(getSession());
	}
}