package com.db4o.replication.hibernate.impl.ref_as_columns;

import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.replication.hibernate.impl.AbstractUpdateEventListener;
import com.db4o.replication.hibernate.impl.Util;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostUpdateEventListener;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class RefAsColumnsUpdateEventListener extends AbstractUpdateEventListener {
// ------------------------------ FIELDS ------------------------------

	protected final Map<Session, Configuration> sessionConfigurationMap = new HashMap();

// --------------------------- CONSTRUCTORS ---------------------------

	public RefAsColumnsUpdateEventListener() {
		//empty
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface UpdateEventListener ---------------------


	public void configure(Configuration cfg) {
		new RefAsColumnsTablesCreator(RefAsColumnsConfiguration.produce(cfg)).createTables();
		cfg.setInterceptor(this);
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostUpdateEventListeners(new PostUpdateEventListener[]{this});
	}

	public void install(Session session, Configuration cfg) {
		super.install(session, cfg);
		sessionConfigurationMap.put(session, cfg);
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