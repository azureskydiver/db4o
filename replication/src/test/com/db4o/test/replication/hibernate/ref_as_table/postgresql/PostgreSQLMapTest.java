package com.db4o.test.replication.hibernate.ref_as_table.postgresql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.collections.map.MapTest;
import com.db4o.test.replication.hibernate.HibernateUtil;
import org.hibernate.cfg.Configuration;

public class PostgreSQLMapTest extends MapTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateUtil.producePostgreSQLConfigA();
		configuration.addClass(MapHolder.class);
		configuration.addClass(MapContent.class);
		return new RefAsColumnsReplicationProvider(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateUtil.producePostgreSQLConfigB();
		configuration.addClass(MapHolder.class);
		configuration.addClass(MapContent.class);
		return new RefAsColumnsReplicationProvider(configuration, "B");
	}

	public void test() {
		super.test();
	}
}
