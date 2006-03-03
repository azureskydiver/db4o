package com.db4o.test.replication.hibernate;

import com.db4o.replication.hibernate.MetadataProviderReplicationConfiguration;
import com.db4o.replication.hibernate.SchemaValidator;
import com.db4o.replication.hibernate.metadata.MetaDataTablesCreator;
import com.db4o.test.Test;
import org.hibernate.cfg.Configuration;

public abstract class AbstractMetaDataTablesCreatorTest {
	public AbstractMetaDataTablesCreatorTest() {
	}

	public void testCreate() {
		Configuration cfg = createCfg();
		MetadataProviderReplicationConfiguration rc = MetadataProviderReplicationConfiguration.produce(cfg);
		final MetaDataTablesCreator creator = new MetaDataTablesCreator(rc);

		creator.execute();

		SchemaValidator validator = new SchemaValidator(rc);

		validator.validate();
		validator.destroy();
	}

	public void testValidate() {
		Configuration cfg = validateCfg();
		MetadataProviderReplicationConfiguration rc = MetadataProviderReplicationConfiguration.produce(cfg);
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
