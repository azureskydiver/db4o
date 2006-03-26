package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.provider.Car;
import com.db4o.test.replication.provider.Pilot;
import com.db4o.test.replication.template.r0tor4.R0;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class HibernateUtil {
// ------------------------------ FIELDS ------------------------------

	public static final Class[] mappings;
	private static final String HSQL_CFG_XML = "com/db4o/test/replication/hibernate/hibernate-HSQL.cfg.xml";

	protected static final String JDBC_URL_HEAD = "jdbc:hsqldb:mem:unique_";

	protected static int jdbcUrlCounter = 0;

	private static final String refAsTableA = createNewDbConfig().getProperty(Environment.URL);
	private static final String refAsTableB = createNewDbConfig().getProperty(Environment.URL);

// -------------------------- STATIC METHODS --------------------------

	static {
		mappings = new Class[]{CollectionHolder.class, Replicated.class,
				SPCParent.class, SPCChild.class,
				ListHolder.class, ListContent.class,
				MapHolder.class, MapContent.class,
				SimpleArrayContent.class, SimpleArrayHolder.class,
				R0.class, Pilot.class, Car.class};
	}

	public static Configuration addAllMappings(Configuration cfg) {
		for (int i = 0; i < mappings.length; i++) {
			cfg.addClass(mappings[i]);
		}
		return cfg;
	}

	public static HibernateReplicationProvider refAsTableProviderA() {
		return new HibernateReplicationProviderImpl(addAllMappings(reuse(refAsTableA)), "refAsTableA");
	}

	public static HibernateReplicationProvider refAsTableProviderB() {
		return new HibernateReplicationProviderImpl(addAllMappings(reuse(refAsTableB)), "refAsTableB");
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
		return addAllMappings(new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-A.cfg.xml"));
	}

	public static Configuration producePostgreSQLConfigB() {
		return addAllMappings(new Configuration().configure("com/db4o/test/replication/hibernate/postgresql/hibernate-PostgreSQL-B.cfg.xml"));
	}

	public static Configuration produceMySQLConfigA() {
		return addAllMappings(new Configuration().configure("com/db4o/test/replication/hibernate/mysql/hibernate-MySQL-A.cfg.xml"));
	}

	public static Configuration produceMySQLConfigB() {
		return addAllMappings(new Configuration().configure("com/db4o/test/replication/hibernate/mysql/hibernate-MySQL-B.cfg.xml"));
	}

	public static Configuration produceOracleConfigA() {
		Configuration cfg = new Configuration().configure("com/db4o/test/replication/hibernate/oracle/hibernate-Oracle-A.cfg.xml");
		return addAllMappings(cfg);
	}

	public static Configuration produceOracleConfigB() {
		//Uncomment if you have 2 instances of Oracle on 2 machines
		//return new Configuration().configure("com/db4o/test/replication/hibernate/oracle/hibernate-Oracle-B.cfg.xml");
		return addAllMappings(createNewDbConfig());
	}
}
