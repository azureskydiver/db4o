package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.impl.Util;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.collections.ListTest;
import org.hibernate.cfg.Configuration;

public abstract class HibernateListTest extends ListTest {
	protected Configuration addClasses(Configuration cfg) {
		Util.addClass(cfg, ListHolder.class);
		Util.addClass(cfg, ListContent.class);
		return cfg;
	}
}
