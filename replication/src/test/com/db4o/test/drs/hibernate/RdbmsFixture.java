/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.drs.hibernate;

import org.hibernate.cfg.Configuration;

import com.db4o.test.drs.DrsFixture;
import com.db4o.test.replication.CollectionHolder;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.ReplicationTestCase;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.collections.SimpleArrayContent;
import com.db4o.test.replication.collections.SimpleArrayHolder;
import com.db4o.test.replication.collections.map.MapContent;
import com.db4o.test.replication.collections.map.MapHolder;
import com.db4o.test.replication.provider.Car;
import com.db4o.test.replication.provider.Pilot;
import com.db4o.test.replication.r0tor4.R0;

public abstract class RdbmsFixture implements DrsFixture {
	public static final Class[] mappings;
	static {
		mappings = new Class[]{CollectionHolder.class, Replicated.class,
				SPCParent.class, SPCChild.class,
				ListHolder.class, ListContent.class,
				MapHolder.class,  MapContent.class,
				SimpleArrayContent.class, SimpleArrayHolder.class,
				R0.class, Pilot.class, Car.class};
	}
	
	public static Configuration addAllMappings(Configuration cfg) {
		for (int i = 0; i < ReplicationTestCase.mappings.length; i++) {
			cfg.addClass(ReplicationTestCase.mappings[i]);
		}
		return cfg;
	}
	
	protected Configuration createConfig() {
		Configuration configuration = new Configuration();
		return addAllMappings(configuration);
	}
}
