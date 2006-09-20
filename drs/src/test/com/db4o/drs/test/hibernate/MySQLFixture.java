/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test.hibernate;

import com.db4o.drs.hibernate.impl.HibernateReplicationProviderImpl;

public class MySQLFixture extends RdbmsFixture {
	public MySQLFixture(String name) {
		super(name);
	}

	public void open()  {
		config = createConfig().configure("com/db4o/test/drs/hibernate/MySQL.cfg.xml");
		_provider = new HibernateReplicationProviderImpl(config, _name);
	}
}
