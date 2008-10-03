/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;
import com.db4o.io.*;

import db4ounit.*;

public class ClientTransactionHandleTestCase implements TestLifeCycle {

	public void testHandles() {
		Configuration config = Db4o.newConfiguration();
		config.io(new MemoryIoAdapter());
		final LocalObjectContainer db = (LocalObjectContainer) Db4o.openFile(config, SwitchingFilesFromClientUtil.MAINFILE_NAME);
		final ClientTransactionPool pool = new ClientTransactionPool(db);
		try {
			ClientTransactionHandle handleA = new ClientTransactionHandle(pool);
			Assert.areEqual(db, handleA.transaction().container());
			ClientTransactionHandle handleB = new ClientTransactionHandle(pool);
			Assert.areNotEqual(handleA.transaction(), handleB.transaction());
			Assert.areEqual(db, handleB.transaction().container());
			Assert.areEqual(1, pool.openFileCount());
			
			handleA.acquireTransactionForFile(SwitchingFilesFromClientUtil.FILENAME_A);
			Assert.areEqual(2, pool.openFileCount());
			Assert.areNotEqual(db, handleA.transaction().container());
			handleB.acquireTransactionForFile(SwitchingFilesFromClientUtil.FILENAME_A);
			Assert.areEqual(2, pool.openFileCount());
			Assert.areNotEqual(handleA.transaction(), handleB.transaction());
			Assert.areEqual(handleA.transaction().container(), handleB.transaction().container());
			
			handleA.releaseTransaction();
			Assert.areEqual(db, handleA.transaction().container());
			Assert.areNotEqual(db, handleB.transaction().container());
			Assert.areEqual(2, pool.openFileCount());
			handleB.releaseTransaction();
			Assert.areEqual(db, handleB.transaction().container());
			Assert.areEqual(1, pool.openFileCount());
		}
		finally {
			pool.close();
		}
	}

	public void setUp() throws Exception {
		SwitchingFilesFromClientUtil.deleteFiles();
	}

	public void tearDown() throws Exception {
		SwitchingFilesFromClientUtil.deleteFiles();
	}

}
