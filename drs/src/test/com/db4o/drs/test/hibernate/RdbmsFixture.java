/* Copyright (C) 2004 - 2008  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
package com.db4o.drs.test.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import com.db4o.drs.hibernate.impl.ReplicationConfiguration;
import com.db4o.drs.inside.TestableReplicationProviderInside;
import com.db4o.drs.test.Car;
import com.db4o.drs.test.CollectionHolder;
import com.db4o.drs.test.DrsFixture;
import com.db4o.drs.test.IByteArrayHolder;
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
import com.db4o.drs.test.SimpleItem;
import com.db4o.drs.test.SimpleListHolder;

public abstract class RdbmsFixture implements DrsFixture {
	public static final Class[] mappings;
	
	protected String _name;
	
	protected TestableReplicationProviderInside _provider;
	
	protected Configuration config;
	
	protected String dbUrl;
	
	static {
		mappings = new Class[]{
				IByteArrayHolder.class,
				CollectionHolder.class, Replicated.class,
				SPCParent.class, SPCChild.class,
				ListHolder.class, ListContent.class,
				SimpleListHolder.class, SimpleItem.class,				
				MapHolder.class,  MapContent.class,
				SimpleArrayContent.class, SimpleArrayHolder.class,
				R0.class, Pilot.class, Car.class,
                com.db4o.drs.test.regression.NewPilot.class};
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
		if (_provider==null)
			throw new RuntimeException(
					"Fixture is not yet openned or has already been closed. " +
					"It maybe caused by a replicationSession.close() call in test. " +
					"You should never call replicationSession.close()");
		
		_provider.destroy();
		_provider=null;
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
