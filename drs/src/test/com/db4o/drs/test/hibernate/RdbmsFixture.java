/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.db4o.drs.hibernate.impl.ReplicationConfiguration;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.drs.test.Car;
import com.db4o.drs.test.CollectionHolder;
import com.db4o.drs.test.DrsFixture;
import com.db4o.drs.test.ListContent;
import com.db4o.drs.test.ListHolder;
import com.db4o.drs.test.MapContent;
import com.db4o.drs.test.MapHolder;
import com.db4o.drs.test.Pilot;
import com.db4o.drs.test.R0;
import com.db4o.drs.test.Replicated;
import com.db4o.drs.test.SPCChild;
import com.db4o.drs.test.SPCParent;
import com.db4o.drs.test.SimpleArrayContent;
import com.db4o.drs.test.SimpleArrayHolder;

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
		if (config==null) 
			return;
		
		new SchemaExport(config).drop(false, true);
	}
	
	public void close() {
		_provider.destroy();
	}

	public TestableReplicationProviderInside provider() {
		return _provider;
	}
	
	protected Configuration createConfig() {
		Configuration tmp = new Configuration();
		addAllMappings(tmp);
		return ReplicationConfiguration.decorate(tmp);
	}
}
