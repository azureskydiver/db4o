/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateListTest;
import com.db4o.test.replication.hibernate.HibernateUtil;

public class ListTestHsqlRefAsColumns extends HibernateListTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return HibernateUtil.refAsColumnsProviderA();
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return HibernateUtil.refAsColumnsProviderB();
	}

	public void test() {
		super.test();
	}
}
