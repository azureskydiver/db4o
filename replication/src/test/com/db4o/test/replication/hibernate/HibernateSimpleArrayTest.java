/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.SimpleArrayTest;
import org.hibernate.cfg.Configuration;


public class HibernateSimpleArrayTest extends SimpleArrayTest {

	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
		return new HibernateReplicationProviderImpl(configuration, "B");
	}

	public void test() {
		super.test();
	}

}
