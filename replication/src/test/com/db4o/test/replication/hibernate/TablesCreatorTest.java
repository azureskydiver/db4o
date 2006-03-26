package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import com.db4o.replication.hibernate.impl.TablesCreatorImpl;
import com.db4o.test.Test;
import org.hibernate.cfg.Configuration;

public class TablesCreatorTest {
// --------------------------- CONSTRUCTORS ---------------------------

	public TablesCreatorTest() {
	}

	protected void clean() {

	}

	protected Configuration createCfg() {
		return HibernateUtil.createNewDbConfig();
	}

	protected Configuration validateCfg() {
		return HibernateUtil.createNewDbConfigNotCreateTables();
	}

	public void test() {
		clean();
		tstValidate();
		tstCreate();
	}

	public void tstCreate() {
		Configuration cfg = createCfg();
		final TablesCreatorImpl creator = new TablesCreatorImpl(ReplicationConfiguration.decorate(cfg));

		creator.createTables();
	}

	public void tstValidate() {
		Configuration cfg = validateCfg();

		final TablesCreatorImpl creator = new TablesCreatorImpl(ReplicationConfiguration.decorate(cfg));

		boolean exception = false;
		try {
			creator.createTables();
		} catch (RuntimeException e) {
			exception = true;
		}

		Test.ensure(exception);
	}
}
