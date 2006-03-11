package com.db4o.test.replication.hibernate.ref_as_columns.postgresql;

import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class PostgreSQLMetaDataTablesCreatorTest extends RefAsColumnsMetaDataTablesCreatorTest {
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
