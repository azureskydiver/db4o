package com.db4o.test.replication.hibernate.oracle;

import com.db4o.test.replication.hibernate.AbstractMetaDataTablesCreatorTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class OracleMetaDataTablesCreatorTest extends AbstractMetaDataTablesCreatorTest {
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
