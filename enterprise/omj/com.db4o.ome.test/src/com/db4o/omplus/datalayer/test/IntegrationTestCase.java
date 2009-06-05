/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.datalayer.test;

import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

import com.db4o.omplus.*;
import com.db4o.omplus.datalayer.*;

public class IntegrationTestCase {

	private static final String KEY = "key";

	@Test
	public void testAppDataStore() {
		OMEDataStore dataStore = Activator.getDefault().getOMEDataStore();
		IDbInterface db = Activator.getDefault().getDatabaseInterface();
		db.setDB(null, "a");
		dataStore.setContextLocalEntry(KEY, new ArrayList<String>());
		assertNotNull(dataStore.getContextLocalEntry(KEY));
		db.setDB(null, "b");
		assertNull(dataStore.getContextLocalEntry(KEY));
		db.setDB(null, "a");
		assertNotNull(dataStore.getContextLocalEntry(KEY));
	}
	
}
