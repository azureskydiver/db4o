/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.jdk14;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.HibernateReplicationProviderImpl;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;
import com.db4o.test.replication.db4o.Db4oReplicationTestUtil;
import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.template.SimpleParentChild;
import org.hibernate.cfg.Configuration;

public class SimpleParentChildCombinations extends SimpleParentChild {
	public void test() {
		super.test();
	}

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		a = new HibernateReplicationProviderImpl(newCfg(), "HSQL RefAsTable");
		b = Db4oReplicationTestUtil.newProviderA();
		addProviderPairs(a, b);

		a = Db4oReplicationTestUtil.newProviderA();
		b = new HibernateReplicationProviderImpl(newCfg(), "HSQL RefAsTable");
		addProviderPairs(a, b);

		a = new HibernateReplicationProviderImpl(newCfg(), "HSQL RefAsTable");
		b = new HibernateReplicationProviderImpl(newCfg(), "HSQL RefAsTable");
		addProviderPairs(a, b);
	}

	protected Configuration newCfg() {
		Configuration cfg;
		cfg = HibernateUtil.createNewDbConfig();
		cfg.addClass(SPCParent.class);
		cfg.addClass(SPCChild.class);
		return cfg;
	}

	@Override
	protected TestableReplicationProviderInside prepareProviderA() {
		throw new RuntimeException("REVISE");
	}

	@Override
	protected TestableReplicationProviderInside prepareProviderB() {
		throw new RuntimeException("REVISE");
	}

}
