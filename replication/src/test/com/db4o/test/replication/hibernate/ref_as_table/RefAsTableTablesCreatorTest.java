package com.db4o.test.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.impl.ref_as_table.RefAsTableConfiguration;
import com.db4o.replication.hibernate.impl.ref_as_table.RefAsTableTablesCreator;
import com.db4o.test.Test;
import org.hibernate.cfg.Configuration;

public abstract class RefAsTableTablesCreatorTest {
	public RefAsTableTablesCreatorTest() {
	}

	public void test() {
		tstValidate();
		tstCreate();
	}

	public void tstCreate() {
		Configuration cfg = createCfg();
		RefAsTableConfiguration rc = RefAsTableConfiguration.produce(cfg);
		final RefAsTableTablesCreator creator = new RefAsTableTablesCreator(rc);

		creator.createTables();
	}

	public void tstValidate() {
		Configuration cfg = validateCfg();

		RefAsTableConfiguration rc = RefAsTableConfiguration.produce(cfg);
		final RefAsTableTablesCreator creator = new RefAsTableTablesCreator(rc);

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
