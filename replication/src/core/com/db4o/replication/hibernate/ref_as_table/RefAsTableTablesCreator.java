package com.db4o.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.RefConfig;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaValidator;

public class RefAsTableTablesCreator {
	RefConfig cfg;

	protected SchemaValidator validator;

	public RefAsTableTablesCreator(RefConfig aCfg) {
		cfg = aCfg;
		validator = new SchemaValidator(cfg.getConfiguration());
	}

	/**
	 * @throws RuntimeException when tables/columns not found
	 */
	public void execute() {
		if (cfg.getConfiguration().getProperties().get(Environment.HBM2DDL_AUTO).equals("validate"))
			validator.validate();
		else {
			SessionFactory sessionFactory = cfg.getConfiguration(). buildSessionFactory();
			sessionFactory.close();
			validator.validate();
		}
	}
}
