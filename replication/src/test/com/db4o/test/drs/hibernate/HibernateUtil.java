package com.db4o.test.drs.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class HibernateUtil {
	private static final String HSQL_CFG_XML = "com/db4o/test/drs/hibernate/hibernate-HSQL.cfg.xml";

	protected static final String JDBC_URL_HEAD = "jdbc:hsqldb:mem:unique_abc_";

	protected static int jdbcUrlCounter = 1000;

	/**
	 * Create a unique Configuration with the underlying database guaranteed to be
	 * empty.
	 *
	 * @return configuration
	 */
	public static Configuration createNewDbConfig() {
		Configuration configuration = new Configuration().configure(HSQL_CFG_XML);
		String url = JDBC_URL_HEAD + jdbcUrlCounter++;
		return configuration.setProperty(Environment.URL, url);
	}

	public static Configuration reuse(String url) {
		Configuration configuration = new Configuration().configure(HSQL_CFG_XML);
		return configuration.setProperty(Environment.URL, url);
	}

	public static Configuration producePostgreSQLConfigA() {
		return new Configuration().configure("com/db4o/test/drs/hibernate/postgresql/hibernate-PostgreSQL-A.cfg.xml");
	}

	public static Configuration producePostgreSQLConfigB() {
		return new Configuration().configure("com/db4o/test/drs/hibernate/postgresql/hibernate-PostgreSQL-B.cfg.xml");
	}

	public static Configuration produceMySQLConfigA() {
		return new Configuration().configure("com/db4o/test/drs/hibernate/mysql/hibernate-MySQL-A.cfg.xml");
	}

	public static Configuration oracleConfigA() {
		return new Configuration().configure("com/db4o/test/drs/hibernate/oracle/hibernate-Oracle-A.cfg.xml");
	}
}
