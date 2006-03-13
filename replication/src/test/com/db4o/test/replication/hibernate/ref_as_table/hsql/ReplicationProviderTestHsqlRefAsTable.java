package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateReplicationProviderTest;
import com.db4o.test.replication.hibernate.ref_as_table.RefAsTableUtil;

public class ReplicationProviderTestHsqlRefAsTable extends HibernateReplicationProviderTest {
	protected TestableReplicationProviderInside prepareSubject() {
		return RefAsTableUtil.newProvider(newCfg(), "subject");
	}
}
