package com.db4o.test.replication.hibernate.ref_as_columns;

import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class RefAsColumnsUtil {
	private static final String dbA;
	private static final String dbB;

	static {
		dbA = HibernateConfigurationFactory.createNewDbConfig().getProperty(Environment.URL);
		dbB = HibernateConfigurationFactory.createNewDbConfig().getProperty(Environment.URL);
	}

	public static HibernateReplicationProvider newProvider(Configuration cfg, String name) {
		return new RefAsColumnsReplicationProvider(cfg, name);
	}

	public static HibernateReplicationProvider newProviderA() {
		Configuration cfg = HibernateConfigurationFactory.reuse(dbA);

		HibernateConfigurationFactory.addAllMappings(cfg);
		return new RefAsColumnsReplicationProvider(cfg, "RefAsColumns A");
	}

	public static HibernateReplicationProvider newProviderB() {
		Configuration cfg = HibernateConfigurationFactory.reuse(dbB);

		HibernateConfigurationFactory.addAllMappings(cfg);
		return new RefAsColumnsReplicationProvider(cfg, "RefAsColumns B");
	}

	public static Configuration getCfgA() {
		Configuration cfg = HibernateConfigurationFactory.reuse(dbA);
		return cfg;
	}

	public static Configuration getCfgB() {
		return HibernateConfigurationFactory.reuse(dbB);
	}
}
