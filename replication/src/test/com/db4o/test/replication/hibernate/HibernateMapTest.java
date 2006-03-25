package com.db4o.test.replication.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.collections.map.MapTest;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class HibernateMapTest extends MapTest {
	protected Configuration cfg = HibernateUtil.refAsTableProviderA().getConfiguration();
	;

	protected TestableReplicationProviderInside prepareProviderA() {
		return HibernateUtil.refAsTableProviderA();
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return HibernateUtil.refAsTableProviderB();
	}

	public void test() {
		//dropTables();
		super.test();
		//dropTables();
	}

	protected void dropTables() {
		final SchemaExport schemaExport = new SchemaExport(cfg);
		schemaExport.setHaltOnError(true);
		schemaExport.drop(false, true);
	}
}
