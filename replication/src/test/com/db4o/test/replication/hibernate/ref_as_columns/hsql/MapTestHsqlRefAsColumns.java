package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateMapTest;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsUtil;
import org.hibernate.cfg.Configuration;

public class MapTestHsqlRefAsColumns extends HibernateMapTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration cfg = HibernateConfigurationFactory.createNewDbConfig();
		addClasses(cfg);
		return RefAsColumnsUtil.newProvider(cfg, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration cfg = HibernateConfigurationFactory.createNewDbConfig();

		addClasses(cfg);
		return RefAsColumnsUtil.newProvider(cfg, "B");
	}
}
