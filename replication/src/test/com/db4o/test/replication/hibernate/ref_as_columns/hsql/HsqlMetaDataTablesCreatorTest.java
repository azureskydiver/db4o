package com.db4o.test.replication.hibernate.ref_as_columns.hsql;

import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsConfiguration;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsSchemaValidator;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsTablesCreator;
import com.db4o.test.Test;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class HsqlMetaDataTablesCreatorTest extends RefAsColumnsMetaDataTablesCreatorTest {
	public HsqlMetaDataTablesCreatorTest() {
	}

	public void testCreate() {
		Configuration positive = createCfg();
		RefAsColumnsConfiguration rc = RefAsColumnsConfiguration.produce(positive);
		final RefAsColumnsTablesCreator creator = new RefAsColumnsTablesCreator(rc);

		creator.execute();

		RefAsColumnsSchemaValidator validator = new RefAsColumnsSchemaValidator(rc);

		validator.validate();
		validator.destroy();
	}

	public void testValidate() {
		Configuration negative = validateCfg();
		RefAsColumnsConfiguration rc = RefAsColumnsConfiguration.produce(negative);
		final RefAsColumnsTablesCreator creator = new RefAsColumnsTablesCreator(rc);

		boolean exception = false;
		try {
			creator.execute();
		} catch (RuntimeException e) {
			exception = true;
		}

		Test.ensure(exception);
	}

	protected Configuration createCfg() {
		return HibernateConfigurationFactory.createNewDbConfig();
	}

	protected Configuration validateCfg() {
		return HibernateConfigurationFactory.createNewDbConfigNotCreateTables();
	}
}
