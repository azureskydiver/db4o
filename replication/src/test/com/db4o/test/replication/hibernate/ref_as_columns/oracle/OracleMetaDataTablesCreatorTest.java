package com.db4o.test.replication.hibernate.ref_as_columns.oracle;

import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.RefAsColumnsMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class OracleMetaDataTablesCreatorTest extends RefAsColumnsMetaDataTablesCreatorTest {
	public void test() {
		super.testValidate();
		super.testCreate();
	}

	protected Configuration createCfg() {
		return HibernateConfigurationFactory.produceOracleConfigA();
	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/oracle/hibernate-Oracle-validate.cfg.xml");
	}
}
