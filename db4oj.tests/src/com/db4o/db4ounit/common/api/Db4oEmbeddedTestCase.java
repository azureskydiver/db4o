package com.db4o.db4ounit.common.api;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.foundation.io.*;

import db4ounit.*;

public class Db4oEmbeddedTestCase extends TestWithTempFile {
	
	public void testOpenFile() {
		final ObjectContainer container = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), _tempFile);
		try {
			Assert.isTrue(File4.exists(_tempFile));
		} finally {
			container.close();
		}
	}
	
	public void testOpenFileWithNullConfiguration() {
		Assert.expect(ArgumentNullException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4oEmbedded.openFile(null, _tempFile);
			}
		});
	}

}
