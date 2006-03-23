package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.replication.hibernate.impl.TablesCreatorImpl;
import com.db4o.test.Test;
import org.hibernate.cfg.Configuration;

public class TablesCreatorTest {
// --------------------------- CONSTRUCTORS ---------------------------

	public TablesCreatorTest() {
	}

	protected Configuration createCfg() {
		return HibernateUtil.createNewDbConfig();
	}

	protected Configuration validateCfg() {
		return HibernateUtil.createNewDbConfigNotCreateTables();
	}

	public void test() {
		tstValidate();
		tstCreate();
	}

	public void tstCreate() {
		Configuration cfg = createCfg();
		ReplicationConfiguration rc = new ReplicationConfiguration(cfg);
		final TablesCreatorImpl creator = new TablesCreatorImpl(rc);

		creator.createTables();
	}

	public void tstValidate() {
		Configuration cfg = validateCfg();

		ReplicationConfiguration rc = new ReplicationConfiguration(cfg);
		final TablesCreatorImpl creator = new TablesCreatorImpl(rc);

		boolean exception = false;
		try {
			creator.createTables();
		} catch (RuntimeException e) {
			exception = true;
		}

		Test.ensure(exception);
	}
}
