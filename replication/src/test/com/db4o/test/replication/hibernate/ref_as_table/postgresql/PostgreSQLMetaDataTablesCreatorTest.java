package com.db4o.test.replication.hibernate.ref_as_table.postgresql;

import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class PostgreSQLMetaDataTablesCreatorTest extends RefAsTableTablesCreatorTest {
	public void test() {
		super.test();
	}

	protected Configuration createCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-A.cfg.xml");
	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-validate.cfg.xml");
	}
}
