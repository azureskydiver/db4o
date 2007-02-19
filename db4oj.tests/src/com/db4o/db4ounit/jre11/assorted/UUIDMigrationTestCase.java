/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.assorted;

import java.io.IOException;

import com.db4o.*;
import com.db4o.db4ounit.common.assorted.UUIDTestItem;
import com.db4o.db4ounit.util.WorkspaceServices;
import com.db4o.foundation.Hashtable4;
import com.db4o.foundation.io.*;

import db4ounit.*;

/**
 * @exclude
 */
public class UUIDMigrationTestCase implements TestCase {
	
	public static void main(String[] args) {
		new TestRunner(UUIDMigrationTestCase.class).run();
	}
	
	public void test() throws Exception {
		configure();
		try {
			final ObjectContainer container = Db4o.openFile(getUUIDMigrationSourcePath());
			try {
				Assert.isNotNull(container);
				
				final Hashtable4 uuidCache = new Hashtable4();
				
				UUIDTestItem.assertItemsCanBeRetrievedByUUID(container.ext(), uuidCache);
				Assert.areEqual(2, uuidCache.size());
				
				UUIDTestItem.assertItemsCanBeRetrievedByUUID(container.ext(), uuidCache);				
			} finally {
				if (null != container) container.close();
			}
			
		} finally {
			restoreConfiguration();
		}
	}
	
	private String getUUIDMigrationSourcePath() throws IOException {
		final String fname = "UUIDMigrationSource-db4o-5.5.yap";
		final String resource = WorkspaceServices.workspaceTestFilePath(fname);
		String targetFile = Path4.combine(Path4.getTempPath(), fname);
		File4.copy(resource, targetFile);
		return targetFile;
	}

	private void restoreConfiguration() {
		Db4o.configure().allowVersionUpdates(false);
		Db4o.configure().generateUUIDs(-1);
	}

	private void configure() {
		Db4o.configure().allowVersionUpdates(true);
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
	}
}
