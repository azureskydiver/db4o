/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.drs.hibernate;

import org.hibernate.cfg.Configuration;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.drs.DrsFixture;
import com.db4o.test.drs.CollectionHolder;
import com.db4o.test.drs.Replicated;
import com.db4o.test.drs.SPCChild;
import com.db4o.test.drs.SPCParent;
import com.db4o.test.drs.ListContent;
import com.db4o.test.drs.ListHolder;
import com.db4o.test.drs.SimpleArrayContent;
import com.db4o.test.drs.SimpleArrayHolder;
import com.db4o.test.drs.MapContent;
import com.db4o.test.drs.MapHolder;
import com.db4o.test.drs.Car;
import com.db4o.test.drs.Pilot;
import com.db4o.test.drs.R0;

public abstract class RdbmsFixture implements DrsFixture {
	public static final Class[] mappings;
	
	protected String _name;
	
	protected TestableReplicationProviderInside _provider;
	
	protected Configuration config;
	
	protected String dbUrl;
	
	static {
		mappings = new Class[]{CollectionHolder.class, Replicated.class,
				SPCParent.class, SPCChild.class,
				ListHolder.class, ListContent.class,
				MapHolder.class,  MapContent.class,
				SimpleArrayContent.class, SimpleArrayHolder.class,
				R0.class, Pilot.class, Car.class};
	}
	
	public static Configuration addAllMappings(Configuration cfg) {
		for (int i = 0; i < mappings.length; i++) {
			cfg.addClass(mappings[i]);
		}
		return cfg;
	}
	
	public RdbmsFixture(String name) {
		_name = name;
	}
	
	public void clean() {
		
	}
	
	public void close() {
		_provider.destroy();
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}
	
	protected Configuration createConfig() {
		Configuration configuration = new Configuration();
		return addAllMappings(configuration);
	}
}
