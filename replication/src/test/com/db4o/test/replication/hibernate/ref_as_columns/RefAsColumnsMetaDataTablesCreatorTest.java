package com.db4o.test.replication.hibernate.ref_as_columns;

import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsConfiguration;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsSchemaValidator;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsTablesCreator;
import com.db4o.test.Test;
import org.hibernate.cfg.Configuration;

public abstract class RefAsColumnsMetaDataTablesCreatorTest {
	public RefAsColumnsMetaDataTablesCreatorTest() {
	}

	public void test() {
		tstValidate();
		tstCreate();
	}

	public void tstCreate() {
		Configuration cfg = createCfg();
		RefAsColumnsConfiguration rc = RefAsColumnsConfiguration.produce(cfg);
		final RefAsColumnsTablesCreator creator = new RefAsColumnsTablesCreator(rc);

		creator.createTables();

		RefAsColumnsSchemaValidator validator = new RefAsColumnsSchemaValidator(rc);

		validator.validate();
		validator.destroy();
	}

	public void tstValidate() {
		Configuration cfg = validateCfg();
		RefAsColumnsConfiguration rc = RefAsColumnsConfiguration.produce(cfg);
		final RefAsColumnsTablesCreator creator = new RefAsColumnsTablesCreator(rc);

		boolean exception = false;
		try {
			creator.createTables();
		} catch (RuntimeException e) {
			exception = true;
		}

		Test.ensure(exception);
	}

	protected abstract Configuration createCfg();

	protected abstract Configuration validateCfg();
}
