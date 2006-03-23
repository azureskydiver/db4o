/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.Test;
import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.SimpleArrayTest;
import com.db4o.test.replication.hibernate.HibernateUtil;
import org.hibernate.cfg.Configuration;


public class Db4oHibernateSimpleArrayTest extends SimpleArrayTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateUtil.createNewDbConfig();
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new Db4oReplicationProvider(Test.objectContainer());
	}

	public void test() {
		super.test();
	}

}
