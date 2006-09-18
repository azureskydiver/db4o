package com.db4o.replication.hibernate.impl;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaValidator;

public final class TablesCreatorImpl implements TablesCreator {
	private final Configuration _cfg;

	public TablesCreatorImpl(Configuration aCfg) {
		_cfg = aCfg;
	}

	/**
	 * @throws RuntimeException when tables/columns not found
	 */
	public final void validateOrCreate() {
		SchemaValidator v = new SchemaValidator(_cfg);

		if (_cfg.getProperties().get(Environment.HBM2DDL_AUTO).equals("validate"))
			v.validate();
		else {
			SessionFactory sessionFactory = _cfg. buildSessionFactory();
			sessionFactory.close();
			v.validate();
		}
	}
}
