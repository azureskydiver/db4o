package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class HsqlMetaDataTablesCreatorTest extends RefAsColumnsMetaDataTablesCreatorTest {
	public HsqlMetaDataTablesCreatorTest() {
	}

	public void test() {
		super.test();
	}

	protected Configuration createCfg() {
		return HibernateConfigurationFactory.createNewDbConfig();
	}

	protected Configuration validateCfg() {
		return HibernateConfigurationFactory.createNewDbConfigNotCreateTables();
	}
}
