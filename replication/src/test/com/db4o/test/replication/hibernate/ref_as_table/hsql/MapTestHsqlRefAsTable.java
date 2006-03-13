package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateMapTest;
import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableUtil;
import org.hibernate.cfg.Configuration;

public class MapTestHsqlRefAsTable extends HibernateMapTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration cfg = RefAsTableUtil.cfgA;
		addClasses(cfg);
		return RefAsTableUtil.newProvider(cfg, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration cfg = RefAsTableUtil.cfgB;
		addClasses(cfg);
		return RefAsTableUtil.newProvider(cfg, "B");
	}

	public void test() {
		super.test();
	}
}
