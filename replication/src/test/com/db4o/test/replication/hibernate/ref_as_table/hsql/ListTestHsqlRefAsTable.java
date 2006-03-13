package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateListTest;
import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableUtil;

public class ListTestHsqlRefAsTable extends HibernateListTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return RefAsTableUtil.newProvider(addClasses(HibernateConfigurationFactory.createNewDbConfig()), "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return RefAsTableUtil.newProvider(addClasses(HibernateConfigurationFactory.createNewDbConfig()), "A");
	}

	public void test() {
		super.test();
	}
}
