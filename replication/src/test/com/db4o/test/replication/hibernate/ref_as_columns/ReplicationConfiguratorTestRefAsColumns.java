package com.db4o.test.replication.hibernate.ref_as_columns;

import com.db4o.replication.ReplicationConfigurator;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.impl.ref_as_columns.Shared;
import com.db4o.replication.hibernate.metadata.Uuid;
import com.db4o.replication.hibernate.cfg.ObjectConfig;
import com.db4o.test.Test;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.hibernate.AbstractReplicationConfiguratorTest;
import com.db4o.test.replication.hibernate.HibernateUtil;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import java.io.Serializable;

public class ReplicationConfiguratorTestRefAsColumns extends AbstractReplicationConfiguratorTest {
	public ReplicationConfiguratorTestRefAsColumns() {

	}

	public void test() {
		super.test();
	}

	protected void init() {
		cfg = HibernateUtil.createNewDbConfig();
		objectConfig = new ObjectConfig(cfg);

		Util.addClass(cfg, CollectionHolder.class);
		ReplicationConfigurator.configure(cfg);
	}

	protected void ensureDeleted(Uuid uuid) {
	}

	protected Uuid getUuid(Session session, Object obj) {
		String tName = objectConfig.getTableName(obj.getClass());
		String pName = objectConfig.getPrimaryKeyColumnName(obj);
		Serializable identifier = session.getIdentifier(obj);

		return (Uuid) Shared.getUuidAndVersion(tName, pName, identifier, session)[0];
	}

	protected Session openSession() {
		Session session = sessionFactory.openSession();
		ReplicationConfigurator.install(session, cfg);
		session.setFlushMode(FlushMode.ALWAYS);
		return session;
	}

	protected void checkVersion(Configuration cfg, Session session, Object obj, long expected) {
		long actual = Shared.getVersion(cfg, session, obj);
		Test.ensureEquals(expected, actual);
	}
}
