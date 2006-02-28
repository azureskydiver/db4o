package com.db4o.test.replication.hibernate.postgresql;

import com.db4o.test.replication.hibernate.AbstractMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class PostgreSQLMetaDataTablesCreatorTest extends AbstractMetaDataTablesCreatorTest {
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
