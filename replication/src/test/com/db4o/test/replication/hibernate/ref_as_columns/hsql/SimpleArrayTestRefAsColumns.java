/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateSimpleArrayTest;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsUtil;

public class SimpleArrayTestRefAsColumns extends HibernateSimpleArrayTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return RefAsColumnsUtil.newProviderA();
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return RefAsColumnsUtil.newProviderB();
	}

	public void test() {
		super.test();
	}
}
