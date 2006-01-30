package com.db4o.test.replication.hibernate;

import org.hibernate.cfg.Configuration;

public class HibernateConfigurationFactory {
	protected static final String JDBC_URL_HEAD = "jdbc:hsqldb:mem:unique_";
	protected static int jdbcUrlCounter = 0;

	/**
	 * Create a unique Configuration with the underlying database guaranteed to be
	 * empty.
	 *
	 * @return configuration
	 */
	public static Configuration createNewDbConfig() {
		Configuration configuration = new Configuration().configure("com/db4o/test/replication/hibernate/hibernate.cfg.xml");
		String url = JDBC_URL_HEAD + jdbcUrlCounter++;
		//System.out.println("url = " + url);
		return configuration.setProperty("hibernate.connection.url", url);
	}
}
