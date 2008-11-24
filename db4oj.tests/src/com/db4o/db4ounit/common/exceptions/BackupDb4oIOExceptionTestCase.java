/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class BackupDb4oIOExceptionTestCase extends Db4oIOExceptionTestCaseBase {
	
	public static void main(String[] args) {
		new BackupDb4oIOExceptionTestCase().runAll();
	}
	
	private static final String BACKUP_FILE = "backup.db4o";

	protected void db4oSetupBeforeStore() throws Exception {
		super.db4oSetupBeforeStore();
		File4.delete(BACKUP_FILE);
	}

	protected void db4oTearDownBeforeClean() throws Exception {
		super.db4oTearDownBeforeClean();
		File4.delete(BACKUP_FILE);
	}
	
	public void testBackup() {
		Assert.expect(Db4oIOException.class, new CodeBlock() {
			public void run() throws Throwable {
				ExceptionSimulatingStorage.exception = true;
				db().backup(BACKUP_FILE);
			}
		});
	}
	
}
