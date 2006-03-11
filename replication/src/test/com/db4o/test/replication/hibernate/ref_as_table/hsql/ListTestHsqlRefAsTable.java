package com.db4o.test.replication.hibernate.ref_as_table.hsql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.collections.ListTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateUtil;
import org.hibernate.cfg.Configuration;

public class ListTestHsqlRefAsTable extends ListTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return HibernateUtil.newRefAsTable(newCfg(), "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return HibernateUtil.newRefAsTable(newCfg(), "B");
	}

	private Configuration newCfg() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);
		return configuration;
	}

	public void test() {
		super.test();
	}
}
