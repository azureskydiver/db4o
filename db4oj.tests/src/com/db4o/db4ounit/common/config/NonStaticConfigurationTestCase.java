/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.config;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class NonStaticConfigurationTestCase implements Db4oTestCase {

	public static void main(String[] args) {
		new TestRunner(NonStaticConfigurationTestCase.class).run();
	}
	
	public void setUp() throws Exception {
		new File(FILENAME).delete();
	}

	public void tearDown() throws Exception {
		new File(FILENAME).delete();
	}

	public static class Data {
		public int id;

		public Data(int id) {
			this.id = id;
		}
	}

	private static final String FILENAME = "nonstaticcfg.yap";

	public void testOpenWithNonStaticConfiguration() {
		final Configuration config1 = Db4o.newConfiguration();
		config1.readOnly(true);
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openFile(config1, FILENAME);
			}
		});
		config1.readOnly(false);
		final ObjectContainer db1 = Db4o.openFile(config1, FILENAME);
		config1.readOnly(true);
		try {
			Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
				public void run() throws Throwable {
					db1.store(new Data(1));
				}
			});
		} finally {
			db1.close();
		}

		Configuration config2 = Db4o.newConfiguration();
		ObjectContainer db2 = Db4o.openFile(config2, FILENAME);
		try {
			db2.store(new Data(2));
			Assert.areEqual(1, db2.query(Data.class).size());
		} finally {
			db2.close();
		}
	}

	public void testIndependentObjectConfigs() {
		Configuration config = Db4o.newConfiguration();
		ObjectClass objectConfig = config.objectClass(Data.class);
		objectConfig.translate(new TNull());
		Configuration otherConfig = Db4o.newConfiguration();
		Assert.areNotSame(config, otherConfig);
		Config4Class otherObjectConfig = (Config4Class) otherConfig
				.objectClass(Data.class);
		Assert.areNotSame(objectConfig, otherObjectConfig);
		Assert.isNull(otherObjectConfig.getTranslator());
	}
}
