/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import java.io.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class BackupExceptionTestCase extends AbstractDb4oTestCase implements
		OptOutCS {

	protected void configure(Configuration config) {
		config.io(new ExceptionIOAdapter());
	}
	
	public static void main(String[] args) {
		new BackupExceptionTestCase().runAll();
	}
	
	private static final String BACKUP_FILE = "backup.db4o";

	protected void db4oSetupBeforeStore() throws Exception {
		ExceptionIOAdapter.exception = false;
		File4.delete(BACKUP_FILE);
	}

	protected void db4oCustomTearDown() throws Exception {
		ExceptionIOAdapter.exception = false;
		File4.delete(BACKUP_FILE);
	}
	
	public void testBackupException() {
		Assert.expect(BackupException.class, IOException.class,
				new CodeBlock() {
					public void run() throws Throwable {
						ExceptionIOAdapter.exception = true;
						db().backup(BACKUP_FILE);
					}
				});
	}
	
}
