/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.SimpleArrayTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;


public class HibernateSimpleArrayTest extends SimpleArrayTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
		return new RefAsColumnsReplicationProvider(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
		return new RefAsColumnsReplicationProvider(configuration, "B");
	}

	public void test() {
		super.test();
	}

}
