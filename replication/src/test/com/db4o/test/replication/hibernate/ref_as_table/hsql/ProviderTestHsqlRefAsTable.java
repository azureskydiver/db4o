package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_table.RefAsTableReplicationProvider;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.ProviderTestRefAsColumns;

public class ProviderTestHsqlRefAsTable extends ProviderTestRefAsColumns {
	protected TestableReplicationProviderInside prepareSubject() {
		return new RefAsTableReplicationProvider(newCfg());
	}

	public void testReplicationProvider() {
		super.testReplicationProvider();
	}
}
