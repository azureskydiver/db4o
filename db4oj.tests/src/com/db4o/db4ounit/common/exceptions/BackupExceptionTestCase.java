/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class BackupExceptionTestCase extends AbstractDb4oTestCase implements
		OptOutCS {

	public static void main(String[] args) {
		new BackupExceptionTestCase().runAll();
	}
	
	private static final String BACKUP_FILE = "backup.db4o";

	protected void db4oSetupBeforeStore() throws Exception {
		File4.delete(BACKUP_FILE);
	}

	protected void db4oCustomTearDown() throws Exception {
		File4.delete(BACKUP_FILE);
	}
	
	/**
	 * @sharpen.ignore
	 */
	public void testBackupException() {
		Assert.expect(BackupException.class, IOException.class,
				new CodeBlock() {
					public void run() throws Throwable {
						db().backup("-:IllegalFile~name^.:)");
					}
				});
	}
	
}
