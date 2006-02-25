/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o.hibernate;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.Test;
import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.SimpleArrayTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;


public class Db4oHibernateSimpleArrayTest extends SimpleArrayTest {

	protected TestableReplicationProvider prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProvider prepareProviderB() {
		return new Db4oReplicationProvider(Test.objectContainer());
	}

	public void test() {
		super.test();
	}

}
