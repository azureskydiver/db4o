package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.collections.map.MapTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class HibernateMapTest extends MapTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		return new RefAsColumnsReplicationProvider(newCfg(), "A");
	}

	protected Configuration newCfg() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(MapHolder.class);
		configuration.addClass(MapContent.class);
		return configuration;
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new RefAsColumnsReplicationProvider(newCfg(), "B");
	}

	public void test() {
		super.test();
	}
}
