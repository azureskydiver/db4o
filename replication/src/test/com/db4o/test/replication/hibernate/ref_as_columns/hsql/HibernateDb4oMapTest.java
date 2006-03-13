package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.test.Test;

public class HibernateDb4oMapTest extends MapTestHsqlRefAsColumns {
	protected TestableReplicationProviderInside prepareProviderB() {
		return new Db4oReplicationProvider(Test.objectContainer());
	}

	public void test() {
		super.test();
	}
}
