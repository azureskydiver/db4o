package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateAfterDeletion;
import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableUtil;
import org.hibernate.cfg.Configuration;

public class AfterDeletionTestHsqlRefAsTable extends HibernateAfterDeletion {
	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration cfg = RefAsTableUtil.getCfgA();
		addClasses(cfg);
		return RefAsTableUtil.newProvider(cfg, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration cfg = RefAsTableUtil.getCfgB();
		addClasses(cfg);
		return RefAsTableUtil.newProvider(cfg, "B");
	}

	public void test() {
		super.test();
	}
}
