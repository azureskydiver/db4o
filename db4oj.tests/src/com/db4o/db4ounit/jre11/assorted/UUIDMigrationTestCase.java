/* Copyright (C) 2006 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre11.assorted;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.assorted.*;
import com.db4o.db4ounit.util.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;

/**
 * @exclude
 */
public class UUIDMigrationTestCase implements TestCase, OptOutNoFileSystemData, OptOutWorkspaceIssue {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(UUIDMigrationTestCase.class).run();
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
		final String fileName = "UUIDMigrationSource-db4o-5.5.db4o";
		final String sourceFile  = WorkspaceServices.workspaceTestFilePath("uuid/" +  fileName);
		String targetFile = IOServices.buildTempPath(fileName); 
		File4.copy(sourceFile, targetFile);
		return targetFile;
	}

	private void restoreConfiguration() {
		Db4o.configure().exceptionsOnNotStorable(true);
		Db4o.configure().allowVersionUpdates(false);
		Db4o.configure().generateUUIDs(ConfigScope.DISABLED);
	}

	private void configure() {
		Db4o.configure().exceptionsOnNotStorable(false);
		Db4o.configure().allowVersionUpdates(true);
		Db4o.configure().generateUUIDs(ConfigScope.GLOBALLY);
	}
}
