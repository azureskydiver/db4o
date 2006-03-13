package com.db4o.test.replication.hibernate.ref_as_columns;

import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class RefAsColumnsUtil {
	public static Configuration cfgA = HibernateConfigurationFactory.createNewDbConfig();
	public static Configuration cfgB = HibernateConfigurationFactory.createNewDbConfig();

	public static HibernateReplicationProvider newProvider(Configuration cfg, String name) {
		return new RefAsColumnsReplicationProvider(cfg, name);
	}
}
