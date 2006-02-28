package com.db4o.test.replication.db4o.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.Test;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.collections.ListTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class HibernateDb4oListTest extends ListTest {

	protected TestableReplicationProviderInside prepareProviderA() {
		return new Db4oReplicationProvider(Test.objectContainer());
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	public void test() {
		super.test();
	}

}
