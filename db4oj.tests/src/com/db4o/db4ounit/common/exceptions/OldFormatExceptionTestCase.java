/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.exceptions;

import java.io.IOException;

import com.db4o.*;
import com.db4o.db4ounit.util.IOServices;
import com.db4o.db4ounit.util.WorkspaceServices;
import com.db4o.ext.OldFormatException;
import com.db4o.foundation.io.File4;
import com.db4o.internal.Platform4;

import db4ounit.*;
import db4ounit.extensions.fixtures.OptOutNoFileSystemData;

/**
 * @exclude
 */
public class OldFormatExceptionTestCase implements TestCase, OptOutNoFileSystemData {

	public static void main(String[] args) {
		new ConsoleTestRunner(OldFormatExceptionTestCase.class).run();
	}
	
	// It is also regression test for COR-634.
	
	public void test() throws Exception {
		if (WorkspaceServices.workspaceRoot() == null) {
			System.err.println("Build environment not available. Skipping test case...");
			return;
		}
	    if (!File4.exists(sourceFile())) {
            System.err.println("Test source file " + sourceFile() + " not available. Skipping test case...");
            return;
        }

		
		Db4o.configure().reflectWith(Platform4.reflectorForType(OldFormatExceptionTestCase.class));
		
		Db4o.configure().allowVersionUpdates(false);
		final String oldDatabaseFilePath = oldDatabaseFilePath();
		
		Assert.expect(OldFormatException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openFile(oldDatabaseFilePath);
			}
		});
		
		Db4o.configure().allowVersionUpdates(true);
		ObjectContainer container = null;
		try {
			container = Db4o.openFile(oldDatabaseFilePath);
		} finally {
			if (container != null) {
				container.close();
			}
		}
	}

	protected String oldDatabaseFilePath() throws IOException {
		final String oldFile = IOServices.buildTempPath("old_db.yap");
		File4.copy(sourceFile(), oldFile);
		return oldFile;
	}
	
	private String sourceFile(){
        return WorkspaceServices.workspaceTestFilePath("db4oVersions/db4o_3.0.3");
    }
	
}
