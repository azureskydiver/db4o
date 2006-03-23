package com.db4o.test.replication.hibernate.oracle;

import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.TablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class OracleMetaDataTablesCreatorTest extends TablesCreatorTest {
	protected Configuration createCfg() {
		return HibernateUtil.produceOracleConfigA();
	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/oracle/hibernate-Oracle-validate.cfg.xml");
	}

	public void test() {
		super.test();
	}
}
