package com.db4o.test.replication.hibernate.ref_as_table.mysql;

import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class MySQLMetaDataTablesCreatorTest extends RefAsTableTablesCreatorTest {
	public void test() {
		super.test();
	}

	protected Configuration createCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/mysql/hibernate-MySQL-A.cfg.xml");

	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/mysql/hibernate-MySQL-validate.cfg.xml");
	}
}
