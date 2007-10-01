/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.db4ounit.common.migration.*;
import com.db4o.defragment.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

/**
 * test case for COR-785
 */
public class LegacyDatabaseDefragTestCase implements TestCase {
	
	public static final class Item {
	}
	
	public void _test() throws Exception {
		final String dbFile = getTempFile();
		createLegacyDatabase(dbFile);
		defrag(dbFile);
	}
	
	public void createDatabase(String fname) {	
		final ObjectContainer container = Db4o.openFile(fname);
		try {
			container.set(new Item());
		} finally {
			container.close();
		}
	}

	private String getTempFile() throws IOException {
		return Path4.getTempFileName();
	}

	private void defrag(String dbFile) throws IOException {
		Defragment.defrag(dbFile);
	}
	
	private void createLegacyDatabase(String dbFile) throws Exception {		
		Db4oLibrary library = new Db4oLibrarian(new Db4oLibraryEnvironmentProvider(PathProvider.testCasePath())).forVersion("6.1");
		library.environment.invokeInstanceMethod(getClass(), "createDatabase", new Object[] { dbFile });
	}

}
