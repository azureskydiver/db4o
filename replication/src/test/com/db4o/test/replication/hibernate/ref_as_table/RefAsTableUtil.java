package com.db4o.test.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.impl.ref_as_table.RefAsTableReplicationProvider;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class RefAsTableUtil {
	private static final String dbA;
	private static final String dbB;


	static {
		dbA = HibernateConfigurationFactory.createNewDbConfig().getProperty(Environment.URL);
		dbB = HibernateConfigurationFactory.createNewDbConfig().getProperty(Environment.URL);
	}

	public static HibernateReplicationProvider newProvider(Configuration cfg, String name) {
		return new RefAsTableReplicationProvider(cfg, name);
	}

	public static Configuration getCfgA() {
		Configuration tmp = HibernateConfigurationFactory.reuse(dbA);
		return tmp;
	}


	public static Configuration getCfgB() {
		Configuration tmp = HibernateConfigurationFactory.reuse(dbB);
		return tmp;
	}
}
