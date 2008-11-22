package com.db4o.db4ounit.common.io;

import com.db4o.db4ounit.common.api.*;
import com.db4o.io.*;

import db4ounit.*;

public class RandomAccessFileStorageFactoryTestCase extends TestWithTempFile {
	
	private final RandomAccessFileStorageFactory subject = new RandomAccessFileStorageFactory();
	
	public void testExistsWithUnexistentFile() {
		Assert.isFalse(subject.exists(_tempFile));
	}
	
	public void testExistsWithZeroLenghtFile() {
		final StorageFactory.Bin storage = subject.open(_tempFile, false, 0, false);
		storage.close();
		Assert.isFalse(subject.exists(_tempFile));
	}

}
