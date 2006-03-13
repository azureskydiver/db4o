package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateAfterDeletion;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsUtil;
import org.hibernate.cfg.Configuration;

public class AfterDeletionHsqlRefAsColumns extends HibernateAfterDeletion {
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
