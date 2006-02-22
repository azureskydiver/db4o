package com.db4o.test.replication.hibernate.oracle;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.collections.map.MapTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class OracleMapTest extends MapTest {

	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.produceOracleConfigA();
		configuration.addClass(MapHolder.class);
		configuration.addClass(MapContent.class);
		return new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.produceOracleConfigB();
		configuration.addClass(MapHolder.class);
		configuration.addClass(MapContent.class);
		return new HibernateReplicationProviderImpl(configuration, "B", new byte[]{2});
	}

	public void test() {
		super.test();
	}
}
