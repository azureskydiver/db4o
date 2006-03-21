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
import java.util.HashMap;
import java.util.Map;

public class RefAsColumnsObjectLifeCycleEventsListener extends AbstractObjectLifeCycleEventsListener {
// ------------------------------ FIELDS ------------------------------

	protected final Map<Session, ObjectConfig> sessionConfigurationMap = new HashMap();

// --------------------------- CONSTRUCTORS ---------------------------

	public RefAsColumnsObjectLifeCycleEventsListener() {
		//empty
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface UpdateEventListener ---------------------


	public void onPostInsert(PostInsertEvent event) {
		Object entity = event.getEntity();
		if (Util.skip(entity)) return;

		Serializable id = event.getId();
		ObjectConfig objectConfig = getObjectConfig();
		String tName = objectConfig.getTableName(entity.getClass());
		String pName = objectConfig.getPrimaryKeyColumnName(entity);

		long ver = Util.getMaxVersion(getSession().connection()) + 1;
		UuidGenerator uuidGenerator = sessionUuidGeneratorMap.get(getSession());
		Uuid uuid = uuidGenerator.next();
		Shared.updateMetadata(ver, uuid.getLongPart(), id, tName, pName, getSession(), uuid.getProvider().getId());
	}

	public void configure(Configuration cfg) {
		new RefAsColumnsTablesCreator(RefAsColumnsConfiguration.produce(cfg)).createTables();
		Util.initMySignature(cfg);

		setListeners(cfg);
	}

	public void install(Session session, Configuration cfg) {
		super.install(session, cfg);
		sessionConfigurationMap.put(session, new ObjectConfig(cfg));
	}

	protected Uuid getUuid(Object entity) {
		ObjectConfig objectConfig = getObjectConfig();
		String tName = objectConfig.getTableName(entity.getClass());
		String pName = objectConfig.getPrimaryKeyColumnName(entity);
		Serializable identifier = getSession().getIdentifier(entity);

		return (Uuid) Shared.getUuidAndVersion(tName, pName, identifier, getSession())[0];
	}

	protected void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();

		long newVersion = Util.getMaxVersion(session.connection()) + 1;
		ObjectConfig objectConfig = getObjectConfig();

		String tableName = objectConfig.getTableName(obj.getClass());
		String primaryKeyColumnName = objectConfig.getPrimaryKeyColumnName(obj);
		Connection connection = session.connection();
		Shared.incrementObjectVersion(connection, id, newVersion, tableName, primaryKeyColumnName);
	}

	protected final ObjectConfig getObjectConfig() {
		return sessionConfigurationMap.get(getSession());
	}
}