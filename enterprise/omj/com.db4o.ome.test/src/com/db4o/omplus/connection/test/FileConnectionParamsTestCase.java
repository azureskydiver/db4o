/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.omplus.connection.test;

import static org.junit.Assert.*;
import static com.db4o.omplus.test.util.Db4oTestUtil.*;

import java.io.*;

import org.hamcrest.*;
import org.junit.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.omplus.connection.*;

public class FileConnectionParamsTestCase {

	@Test
	public void testNotFound() throws IOException {
		File file = nonExistentFile();
		try {
			new FileConnectionParams(file.getAbsolutePath()).connect();
			fail();
		}
		catch(DBConnectException exc) {
			FileNotFoundException cause = (FileNotFoundException) exc.getCause();
			assertEquals(file.getAbsolutePath(), cause.getMessage());
		}
	}

	@Test
	public void testLocked() throws IOException {
		File file = nonExistentFile();
		LocalObjectContainer db = (LocalObjectContainer) Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), file.getAbsolutePath());
		try {
			new FileConnectionParams(file.getAbsolutePath()).connect();
			fail();
		}
		catch(DBConnectException exc) {
			assertThat(exc.getCause(), CoreMatchers.instanceOf(DatabaseFileLockedException.class));
		}
		finally {
			db.close();
			file.delete();
		}
	}

	@Test
	public void testOpen() throws Exception {
		File file = createEmptyDatabase();
		try {
			LocalObjectContainer opened = (LocalObjectContainer) new FileConnectionParams(file.getAbsolutePath()).connect();
			try {
				assertEquals(file.getPath(), opened.fileName());
				assertFalse(opened.config().isReadOnly());
			}
			finally {
				opened.close();
			}
		}
		finally {
			file.delete();
		}
	}

	@Test
	public void testOpenReadOnly() throws Exception {
		File file = createEmptyDatabase();
		try {
			LocalObjectContainer opened = (LocalObjectContainer) new FileConnectionParams(file.getAbsolutePath(), true).connect();
			try {
				assertEquals(file.getPath(), opened.fileName());
				assertTrue(opened.config().isReadOnly());
			}
			finally {
				opened.close();
			}
		}
		finally {
			file.delete();
		}
	}

}
