/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateListTest;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsUtil;
import org.hibernate.cfg.Configuration;

public class ListTestHsqlRefAsColumns extends HibernateListTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration cfg = RefAsColumnsUtil.cfgA;
		addClasses(cfg);
		return RefAsColumnsUtil.newProvider(cfg, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration cfg = RefAsColumnsUtil.cfgB;
		addClasses(cfg);
		return RefAsColumnsUtil.newProvider(cfg, "B");
	}

	public void test() {
		super.test();
	}
}
