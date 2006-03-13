package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_table.RefAsTableReplicationProvider;
import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.SimpleArrayTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class SimpleArrayTestHsqlRefAsTable extends SimpleArrayTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
		return new RefAsTableReplicationProvider(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
		return new RefAsTableReplicationProvider(configuration, "B");
	}

	public void test() {
		super.test();
	}
}


