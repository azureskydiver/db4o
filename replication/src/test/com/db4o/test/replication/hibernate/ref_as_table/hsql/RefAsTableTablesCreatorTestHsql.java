package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class RefAsTableTablesCreatorTestHsql extends RefAsTableTablesCreatorTest {
	protected Configuration createCfg() {
		return HibernateConfigurationFactory.createNewDbConfig();
	}

	protected Configuration validateCfg() {
		return HibernateConfigurationFactory.createNewDbConfigNotCreateTables();
	}
}
