/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateListTest;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsUtil;

public class ListTestHsqlRefAsColumns extends HibernateListTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return RefAsColumnsUtil.newProvider(addClasses(HibernateConfigurationFactory.createNewDbConfig()), "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return RefAsColumnsUtil.newProvider(addClasses(HibernateConfigurationFactory.createNewDbConfig()), "A");
	}

	public void test() {
		super.test();
	}
}
