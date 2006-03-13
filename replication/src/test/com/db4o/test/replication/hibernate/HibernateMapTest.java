package com.db4o.test.replication.hibernate;

import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.collections.map.MapTest;
import org.hibernate.cfg.Configuration;

public abstract class HibernateMapTest extends MapTest {
	protected Configuration addClasses(Configuration cfg) {
		cfg.addClass(MapHolder.class);
		cfg.addClass(MapContent.class);
		return cfg;
	}
}
