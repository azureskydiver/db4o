/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.regression;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.db4ounit.util.IOServices;
import com.db4o.db4ounit.util.WorkspaceServices;
import com.db4o.ext.OldFormatException;
import com.db4o.foundation.io.File4;
import com.db4o.internal.Platform4;

import db4ounit.Assert;
import db4ounit.CodeBlock;
import db4ounit.TestCase;
import db4ounit.extensions.fixtures.OptOutNoFileSystemData;

/**
 * @exclude
 */
public class COR234TestCase implements TestCase, OptOutNoFileSystemData {

	public void test() {
		if (WorkspaceServices.workspaceRoot() == null) {
			System.err.println("Build environment not available. Skipping test case...");
			return;
		}
		if(! File4.exists(sourceFile())){
            System.err.println("Test source file " + sourceFile() + " not available. Skipping test case...");
            return;
		}
		
		Db4o.configure().allowVersionUpdates(false);
		Db4o.configure().reflectWith(Platform4.reflectorForType(COR234TestCase.class));
		
		Assert.expect(OldFormatException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openFile(oldDatabaseFilePath());
			}
		});
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
