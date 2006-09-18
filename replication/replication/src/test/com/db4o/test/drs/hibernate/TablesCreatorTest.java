package com.db4o.test.drs.hibernate;

import org.hibernate.cfg.Configuration;

import com.db4o.replication.hibernate.impl.ReplicationConfiguration;
import com.db4o.replication.hibernate.impl.TablesCreatorImpl;

import db4ounit.Assert;
import db4ounit.TestCase;

public class TablesCreatorTest implements TestCase{
	public TablesCreatorTest() {
	}

	protected Configuration createCfg() {
		return HibernateUtil.createNewDbConfig();
	}

	protected Configuration validateCfg() {
		Configuration configuration = HibernateUtil.createNewDbConfig();
		return configuration.setProperty("hibernate.hbm2ddl.auto", "validate");
	}

	public void test() {
		tstValidate();
		tstCreate();
	}

	public void tstCreate() {
		Configuration cfg = createCfg();
		final TablesCreatorImpl creator = new TablesCreatorImpl(ReplicationConfiguration.decorate(cfg));

		creator.validateOrCreate();
	}

	public void tstValidate() {
		Configuration cfg = validateCfg();

		final TablesCreatorImpl creator = new TablesCreatorImpl(ReplicationConfiguration.decorate(cfg));

		boolean exception = false;
		try {
			creator.validateOrCreate();
		} catch (RuntimeException e) {
			exception = true;
		}

		Assert.isTrue(exception);
	}
}
