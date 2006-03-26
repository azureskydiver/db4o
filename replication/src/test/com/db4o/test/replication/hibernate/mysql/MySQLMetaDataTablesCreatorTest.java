package com.db4o.test.replication.hibernate.mysql;

import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.TablesCreatorTest;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class MySQLMetaDataTablesCreatorTest extends TablesCreatorTest {
	protected void clean() {
		Configuration cfg = validateCfg();
		ReplicationConfiguration.decorate(HibernateUtil.addAllMappings(cfg));
		final SchemaExport schemaExport = new SchemaExport(cfg);
		schemaExport.setHaltOnError(true);
		schemaExport.drop(false, true);
	}

	protected Configuration createCfg() {
		return HibernateUtil.produceMySQLConfigA();
	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/mysql/hibernate-MySQL-validate.cfg.xml");
	}

	public void test() {
		super.test();
	}
}
