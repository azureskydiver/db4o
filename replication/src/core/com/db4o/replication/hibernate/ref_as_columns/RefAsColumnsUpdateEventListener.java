package com.db4o.replication.hibernate.ref_as_columns;

import com.db4o.replication.hibernate.AbstractUpdateEventListener;
import com.db4o.replication.hibernate.ObjectConfig;
import com.db4o.replication.hibernate.common.Common;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostUpdateEventListener;

import java.io.Serializable;
import java.sql.Connection;

public class RefAsColumnsUpdateEventListener extends AbstractUpdateEventListener {

	public RefAsColumnsUpdateEventListener() {
		//empty
	}

	public void configure(Configuration cfg) {
		new RefAsColumnsTablesCreator(RefAsColumnsConfiguration.produce(cfg)).execute();
		cfg.setInterceptor(this);
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostUpdateEventListeners(new PostUpdateEventListener[]{this});
	}

	public void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);
		sessionConfigurationMap.put(session, cfg);
	}

	protected void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();
		if (session == null)
			throw new RuntimeException("Unable to update the version number of an object. Did you forget to call ReplicationConfigurator.install(session, cfg) after opening a session?");

		long newVersion = Common.getMaxVersion(session.connection()) + 1;
		Configuration cfg = getConfiguration();

		ObjectConfig objectConfig = new ObjectConfig(cfg);

		String tableName = objectConfig.getTableName(obj.getClass());
		String primaryKeyColumnName = objectConfig.getPrimaryKeyColumnName(obj);
		Connection connection = session.connection();
		Shared.incrementObjectVersion(connection, id, newVersion, tableName, primaryKeyColumnName);
	}
}