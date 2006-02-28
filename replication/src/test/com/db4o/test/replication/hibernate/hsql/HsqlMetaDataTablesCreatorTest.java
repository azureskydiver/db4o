package com.db4o.test.replication.hibernate.hsql;

import com.db4o.replication.hibernate.MetaDataTablesCreator;
import com.db4o.replication.hibernate.ReplicationConfiguration;
import com.db4o.replication.hibernate.SchemaValidator;
import com.db4o.test.Test;
import com.db4o.test.replication.hibernate.AbstractMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class HsqlMetaDataTablesCreatorTest extends AbstractMetaDataTablesCreatorTest {
	public HsqlMetaDataTablesCreatorTest() {
	}

	public void testCreate() {
		Configuration positive = createCfg();
		ReplicationConfiguration rc = ReplicationConfiguration.produce(positive);
		final MetaDataTablesCreator creator = new MetaDataTablesCreator(rc);

		creator.execute();

		SchemaValidator validator = new SchemaValidator(rc);

		validator.validate();
		validator.destroy();
	}

	public void testValidate() {
		Configuration negative = validateCfg();
		ReplicationConfiguration rc = ReplicationConfiguration.produce(negative);
		final MetaDataTablesCreator creator = new MetaDataTablesCreator(rc);

		boolean exception = false;
		try {
			creator.execute();
		} catch (RuntimeException e) {
			exception = true;
		}

		Test.ensure(exception);
	}

	protected Configuration createCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/hibernate.cfg.xml");
	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/hibernate-validate.cfg.xml");
	}
}
