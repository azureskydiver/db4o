package com.db4o.test.drs.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class HibernateUtil {
	static final String HSQL_CFG_XML = "com/db4o/test/drs/hibernate/Hsql.cfg.xml";

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
}
