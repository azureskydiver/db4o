package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.test.replication.hibernate.TablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class PostgreSQLMetaDataTablesCreatorTest extends TablesCreatorTest {
	protected Configuration createCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-A.cfg.xml");
	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-validate.cfg.xml");
	}

	public void test() {
		super.test();
	}
}
