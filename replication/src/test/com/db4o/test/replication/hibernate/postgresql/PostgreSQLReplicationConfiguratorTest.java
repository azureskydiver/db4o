package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import org.hibernate.cfg.Configuration;

public class PostgreSQLReplicationConfiguratorTest extends ReplicationConfiguratorTest {
	protected Configuration prepareCfg() {
		return HibernateUtil.producePostgreSQLConfigA();
	}

	public void test() {
		super.test();
	}
}
