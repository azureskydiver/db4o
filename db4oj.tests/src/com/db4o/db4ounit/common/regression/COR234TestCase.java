/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.regression;

import com.db4o.Db4o;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.OldFormatException;
import com.db4o.foundation.io.File4;

import db4ounit.*;

/**
 * @exclude
 */
public class COR234TestCase implements TestCase {

	public void test() {
		Db4o.configure().allowVersionUpdates(false);
		
		Assert.expect(OldFormatException.class, new CodeBlock() {
			public void run() throws Exception {
				Db4o.openFile(oldDatabaseFilePath());
			}
		});
	}

	protected String oldDatabaseFilePath() {
		final String oldFile = IOServices.buildTempPath("old_db.yap");
		File4.copy(WorkspaceServices.workspaceTestFilePath("db4oVersions/db4o_3.0.3"), oldFile);
		return oldFile;
	}
}
