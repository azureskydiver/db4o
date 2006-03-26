package com.db4o.test.replication.hibernate.mysql;

import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import org.hibernate.cfg.Configuration;

public class MySQLReplicationConfiguratorTest extends ReplicationConfiguratorTest {
	protected Configuration prepareCfg() {
		return HibernateUtil.produceMySQLConfigA();
	}

	public void test() {
		super.test();
	}
}
