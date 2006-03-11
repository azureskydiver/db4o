package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.replication.hibernate.ref_as_table.RefAsTableReplicationProvider;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
	static Configuration hsqlCfgA = HibernateConfigurationFactory.createNewDbConfig();
	static Configuration hsqlCfgB = HibernateConfigurationFactory.createNewDbConfig();

	public static HibernateReplicationProvider newRefAsTable(Configuration cfg, String name) {
		return new RefAsTableReplicationProvider(cfg, name);
	}

	public static HibernateReplicationProvider newRefAsColumns(Configuration cfg, String name) {
		return new RefAsColumnsReplicationProvider(cfg, name);
	}

	public static Configuration shareHsqlCfgA() {
		return hsqlCfgA;
	}

	public static Configuration shareHsqlCfgB() {
		return hsqlCfgB;
	}

	public static HibernateReplicationProvider refAsTableA(String name) {
		return new RefAsTableReplicationProvider(shareHsqlCfgA(), name);
	}

	public static HibernateReplicationProvider refAsTableB(String name) {
		return new RefAsColumnsReplicationProvider(shareHsqlCfgB(), name);
	}
}
