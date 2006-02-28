package com.db4o.test.replication.hibernate.mysql;

import com.db4o.test.replication.hibernate.AbstractMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class MySQLMetaDataTablesCreatorTest extends AbstractMetaDataTablesCreatorTest {
	public void testCreate() {
		super.testCreate();
	}

	public void testValidate() {
		super.testValidate();
	}

	protected Configuration createCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/mysql/hibernate-MySQL-A.cfg.xml");

	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/mysql/hibernate-MySQL-validate.cfg.xml");
	}
}
