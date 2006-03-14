package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class HsqlMetaDataTablesCreatorTest extends RefAsColumnsMetaDataTablesCreatorTest {
	public HsqlMetaDataTablesCreatorTest() {
	}

	public void test() {
		super.test();
	}

	protected Configuration createCfg() {
		return HibernateUtil.createNewDbConfig();
	}

	protected Configuration validateCfg() {
		return HibernateUtil.createNewDbConfigNotCreateTables();
	}
}
