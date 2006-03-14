package com.db4o.test.replication.hibernate;

import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.SimpleArrayTest;
import org.hibernate.cfg.Configuration;

public abstract class HibernateSimpleArrayTest extends SimpleArrayTest {
	protected void add(Configuration configuration) {
		configuration.addClass(SimpleArrayHolder.class);
		configuration.addClass(SimpleArrayContent.class);
	}
}
