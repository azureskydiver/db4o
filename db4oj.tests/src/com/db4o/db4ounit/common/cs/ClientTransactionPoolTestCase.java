/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.internal.cs.*;
import com.db4o.io.*;

import db4ounit.*;

public class ClientTransactionPoolTestCase implements TestLifeCycle {

	public void testPool() {
		Configuration config = Db4oEmbedded.newConfiguration();
		config.io(new MemoryIoAdapter());
		final LocalObjectContainer db = (LocalObjectContainer) Db4oEmbedded.openFile(config, SwitchingFilesFromClientUtil.MAINFILE_NAME);
		final ClientTransactionPool pool = new ClientTransactionPool(db);
		try {
			Assert.areEqual(1, pool.openFileCount());
			Transaction trans1 = pool.acquire(SwitchingFilesFromClientUtil.MAINFILE_NAME);
			Assert.areEqual(db, trans1.container());			
			Assert.areEqual(1, pool.openFileCount());
			Transaction trans2 = pool.acquire(SwitchingFilesFromClientUtil.FILENAME_A);
			Assert.areNotEqual(db, trans2.container());			
			Assert.areEqual(2, pool.openFileCount());
			Transaction trans3 = pool.acquire(SwitchingFilesFromClientUtil.FILENAME_A);
			Assert.areEqual(trans2.container(), trans3.container());			
			Assert.areEqual(2, pool.openFileCount());
			pool.release(trans3, true);
			Assert.areEqual(2, pool.openFileCount());
			pool.release(trans2, true);
			Assert.areEqual(1, pool.openFileCount());
			pool.release(trans1, true);
			Assert.areEqual(1, pool.openFileCount());
		}
		finally {
			Assert.isFalse(db.isClosed());
			Assert.isFalse(pool.isClosed());
			pool.close();
			Assert.isTrue(db.isClosed());
			Assert.isTrue(pool.isClosed());
			Assert.areEqual(0, pool.openFileCount());
		}
	}

	public void setUp() throws Exception {
		SwitchingFilesFromClientUtil.deleteFiles();
	}

	public void tearDown() throws Exception {
		SwitchingFilesFromClientUtil.deleteFiles();
	}
}
