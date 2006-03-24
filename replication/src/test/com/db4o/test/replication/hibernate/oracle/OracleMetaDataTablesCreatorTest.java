package com.db4o.test.replication.hibernate.oracle;

import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.TablesCreatorTest;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class OracleMetaDataTablesCreatorTest extends TablesCreatorTest {
	protected void clean() {
		Configuration cfg = validateCfg();
		ReplicationConfiguration.decorate(HibernateUtil.addAllMappings(cfg));
		final SchemaExport schemaExport = new SchemaExport(cfg);
		schemaExport.setHaltOnError(true);
		schemaExport.drop(false, true);
	}

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
