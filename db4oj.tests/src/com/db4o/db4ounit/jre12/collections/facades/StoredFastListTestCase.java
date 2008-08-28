/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.jre12.collections.facades;

import com.db4o.collections.facades.*;
import com.db4o.db4ounit.jre12.collections.*;

/**
 * @decaf.ignore.jdk11
 */
public class StoredFastListTestCase extends FastListTestCaseBase {

	public static void main(String[] args) {
		new StoredFastListTestCase().runAll();
	}

	private FastList retrieveFastListFromDatabase() {
		store(new FastList(new MockPersistentList()));
		return (FastList) retrieveOnlyInstance(FastList.class);
	}
	
	protected void db4oSetupBeforeStore() throws Exception {
		init(retrieveFastListFromDatabase());
	}	
	
}
