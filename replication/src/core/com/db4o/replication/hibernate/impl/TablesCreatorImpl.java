package com.db4o.replication.hibernate.impl;

import com.db4o.replication.hibernate.TablesCreator;
import com.db4o.replication.hibernate.cfg.ReplicationConfiguration;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaValidator;

public final class TablesCreatorImpl implements TablesCreator {
// ------------------------------ FIELDS ------------------------------

	private final ReplicationConfiguration cfg;

	private final SchemaValidator validator;

// --------------------------- CONSTRUCTORS ---------------------------

	public TablesCreatorImpl(ReplicationConfiguration aCfg) {
		cfg = aCfg;
		validator = new SchemaValidator(cfg.getConfiguration());
	}

// ------------------------ INTERFACE METHODS ------------------------

// --------------------- Interface TablesCreator ---------------------

	/**
	 * @throws RuntimeException when tables/columns not found
	 */
	public final void createTables() {
		if (cfg.getConfiguration().getProperties().get(Environment.HBM2DDL_AUTO).equals("validate"))
			validator.validate();
		else {
			SessionFactory sessionFactory = cfg.getConfiguration(). buildSessionFactory();
			sessionFactory.close();
			validator.validate();
		}
	}
}
