/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;

import db4ounit.*;

public class NonStaticConfigurationTestCase implements TestCase {

	public static class Data {
		public int id;

		public Data(int id) {
			this.id = id;
		}
	}
	
	private static final String FILENAME = "nonstaticcfg.yap";

	public void testOpenWithNonStaticConfiguration() {
		new File(FILENAME).delete();
		Configuration cfg=Db4o.newConfiguration();
		cfg.readOnly(true);
		ObjectContainer db=Db4o.openFile(cfg,FILENAME);
		try {
			db.set(new Data(1));
		}
		finally {
			db.close();
		}
		cfg=Db4o.newConfiguration();
		db=Db4o.openFile(cfg,FILENAME);
		try {
			db.set(new Data(2));
			Assert.areEqual(1,db.query(Data.class).size());
		}
		finally {
			db.close();
		}
	}
	
}
