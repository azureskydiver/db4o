package com.db4o.test.replication.hibernate.ref_as_table.mysql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.collections.map.MapTest;
import com.db4o.test.replication.hibernate.HibernateUtil;
import org.hibernate.cfg.Configuration;

public class MySQLMapTest extends MapTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateUtil.produceMySQLConfigA();
		configuration.addClass(MapHolder.class);
		configuration.addClass(MapContent.class);
		return new RefAsColumnsReplicationProvider(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateUtil.produceMySQLConfigB();
		configuration.addClass(MapHolder.class);
		configuration.addClass(MapContent.class);
		return new RefAsColumnsReplicationProvider(configuration, "B");
	}

	public void test() {
		super.test();
	}
}
