package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.impl.HibernateReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.ReplicationTestCase;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class HibernateUtil {
// ------------------------------ FIELDS ------------------------------

	private static final String HSQL_CFG_XML = "com/db4o/test/replication/hibernate/hibernate-HSQL.cfg.xml";

	protected static final String JDBC_URL_HEAD = "jdbc:hsqldb:mem:unique_";

	protected static int jdbcUrlCounter = 0;

	private static final String aUrl = createNewDbConfig().getProperty(Environment.URL);
	private static final String bUrl = createNewDbConfig().getProperty(Environment.URL);

// -------------------------- STATIC METHODS --------------------------

	public static Configuration addAllMappings(Configuration cfg) {
		for (int i = 0; i < ReplicationTestCase.mappings.length; i++) {
			cfg.addClass(ReplicationTestCase.mappings[i]);
		}
		return cfg;
	}

	public static HibernateReplicationProvider newProviderA() {
		return new HibernateReplicationProviderImpl(addAllMappings(reuse(aUrl)), "Hibernate providerA");
	}

	public static HibernateReplicationProvider newProviderB() {
		return new HibernateReplicationProviderImpl(addAllMappings(reuse(bUrl)), "Hibernate providerB");
	}

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

	public static Configuration createNewDbConfigNotCreateTables() {
		Configuration configuration = createNewDbConfig();
		return configuration.setProperty("hibernate.hbm2ddl.auto", "validate");
	}

	public static Configuration producePostgreSQLConfigA() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-A.cfg.xml");
	}

	public static Configuration producePostgreSQLConfigB() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-B.cfg.xml");
	}

	public static Configuration produceMySQLConfigA() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/mysql/hibernate-MySQL-A.cfg.xml");
	}

	public static HibernateReplicationProvider newOracleProviderA() {
		return new HibernateReplicationProviderImpl(addAllMappings(oracleConfigA()), "Oracle A");
	}

	public static Configuration oracleConfigA() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/oracle/hibernate-Oracle-A.cfg.xml");
	}
}
