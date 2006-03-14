package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.impl.Util;
import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.collections.map.MapTest;
import org.hibernate.cfg.Configuration;

public abstract class HibernateMapTest extends MapTest {
	protected Configuration add(Configuration cfg) {
		Util.addClass(cfg, MapHolder.class);
		Util.addClass(cfg, MapContent.class);
		return cfg;
	}
}
