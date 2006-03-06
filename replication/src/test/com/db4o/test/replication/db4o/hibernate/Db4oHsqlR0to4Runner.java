/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o.hibernate;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.Test;
import com.db4o.test.replication.R0;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateR0to4Runner;


public class Db4oHsqlR0to4Runner extends HibernateR0to4Runner {
	protected TestableReplicationProviderInside prepareProviderA() {
		cfgA = HibernateConfigurationFactory.createNewDbConfig();
		cfgA.addClass(R0.class);
		return new RefAsColumnsReplicationProvider(cfgA, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		ExtObjectContainer ocB = Test.objectContainer();
		return new Db4oReplicationProvider(ocB);
	}

	public void test() {
		super.test();
	}

	protected void clean() {
		dropTables(cfgA);
		delete(peerB);
	}
}
