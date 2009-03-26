/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.backup;

import com.db4o.internal.*;
import com.db4o.io.*;

public class BackupMemoryToFileTestCase extends MemoryBackupTestCaseBase {

	@Override
	protected void backup(LocalObjectContainer origDb, String backupPath) {
		origDb.backup(backupStorage(), backupPath);
	}

	@Override
	protected Storage backupStorage() {
		return new FileStorage();
	}

	@Override
	protected Storage origStorage() {
		return new MemoryStorage();
	}
	
}
