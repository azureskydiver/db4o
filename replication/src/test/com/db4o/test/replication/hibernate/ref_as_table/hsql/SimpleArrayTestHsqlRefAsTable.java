package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateSimpleArrayTest;
import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableUtil;
import org.hibernate.cfg.Configuration;

public class SimpleArrayTestHsqlRefAsTable extends HibernateSimpleArrayTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration cfg = RefAsTableUtil.getCfgA();
		add(cfg);
		return RefAsTableUtil.newProvider(cfg, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration cfg = RefAsTableUtil.getCfgB();
		add(cfg);
		return RefAsTableUtil.newProvider(cfg, "B");
	}

	public void test() {
		super.test();
	}
}


