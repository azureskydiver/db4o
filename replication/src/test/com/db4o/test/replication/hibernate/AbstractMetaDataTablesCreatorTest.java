package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.ref_as_columns.MetaDataTablesCreator;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsConfiguration;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsSchemaValidator;
import com.db4o.test.Test;
import org.hibernate.cfg.Configuration;

public abstract class AbstractMetaDataTablesCreatorTest {
	public AbstractMetaDataTablesCreatorTest() {
	}

	public void testCreate() {
		Configuration cfg = createCfg();
		RefAsColumnsConfiguration rc = RefAsColumnsConfiguration.produce(cfg);
		final MetaDataTablesCreator creator = new MetaDataTablesCreator(rc);

		creator.execute();

		RefAsColumnsSchemaValidator validator = new RefAsColumnsSchemaValidator(rc);

		validator.validate();
		validator.destroy();
	}

	public void testValidate() {
		Configuration cfg = validateCfg();
		RefAsColumnsConfiguration rc = RefAsColumnsConfiguration.produce(cfg);
		final MetaDataTablesCreator creator = new MetaDataTablesCreator(rc);

		boolean exception = false;
		try {
			creator.execute();
		} catch (RuntimeException e) {
			exception = true;
		}

		Test.ensure(exception);
	}

	protected abstract Configuration createCfg();

	protected abstract Configuration validateCfg();
}
