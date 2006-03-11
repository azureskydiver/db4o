package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.HibernateReplicationProviderTest;

public class ReplicationProviderTestHsqlRefAsTable extends HibernateReplicationProviderTest {
	protected TestableReplicationProviderInside prepareSubject() {
		return HibernateUtil.newRefAsTable(newCfg(), "subject");
	}
}
