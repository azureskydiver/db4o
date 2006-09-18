package com.db4o.test.replication.hibernate.oracle;

import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.ReplicationConfiguratorTest;
import org.hibernate.cfg.Configuration;

public class OracleReplicationConfiguratorTest extends ReplicationConfiguratorTest {
	protected Configuration prepareCfg() {
		return HibernateUtil.oracleConfigA();
	}

	public void test() {
		super.test();
	}
}
