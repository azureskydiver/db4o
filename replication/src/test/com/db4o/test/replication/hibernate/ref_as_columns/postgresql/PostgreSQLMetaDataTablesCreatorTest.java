package com.db4o.test.replication.hibernate.ref_as_columns.postgresql;

import com.db4o.test.replication.hibernate.ref_as_columns.hsql.RefAsColumnsMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class PostgreSQLMetaDataTablesCreatorTest extends RefAsColumnsMetaDataTablesCreatorTest {
	public void testCreate() {
		super.testCreate();
	}

	public void testValidate() {
		super.testValidate();
	}

	protected Configuration createCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-A.cfg.xml");

	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-validate.cfg.xml");
	}
}
