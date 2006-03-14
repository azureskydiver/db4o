package com.db4o.test.replication.hibernate.ref_as_table;

import com.db4o.replication.ReplicationConfigurator;
import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.impl.ref_as_table.Shared;
import com.db4o.test.Test;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.hibernate.AbstractReplicationConfiguratorTest;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

public class ReplicationConfiguratorTestRefAsTable extends AbstractReplicationConfiguratorTest {
	public ReplicationConfiguratorTestRefAsTable() {

	}

	public void test() {
		super.test();
	}

	protected void init() {
		cfg = RefAsTableUtil.getCfgA();
		Util.addClass(cfg, CollectionHolder.class);
		ReplicationConfigurator.refAsTableConfigure(cfg);
	}

	protected Session openSession() {
		Session session = sessionFactory.openSession();
		ReplicationConfigurator.refAsTableInstall(session, cfg);
		return session;
	}

	protected void checkVersion(Configuration cfg, Session session, Object obj, long expected) {
		long actual = Shared.getVersion(session.connection(), obj.getClass().getName(), Shared.castAsLong(session.getIdentifier(obj)));
		Test.ensureEquals(expected, actual);
	}
}
