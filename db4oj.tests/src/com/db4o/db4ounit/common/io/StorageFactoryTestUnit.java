package com.db4o.db4ounit.common.io;

import com.db4o.db4ounit.common.api.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class StorageFactoryTestUnit extends TestWithTempFile {
	
	public void testInitialLength() {
		
		factory().open(_tempFile, false, 1000, false).close();
		
		final Storage storage = factory().open(_tempFile, false, 0, false);
		try {
			Assert.areEqual(1000, storage.length());
		} finally {
			storage.close();
		}
		
	}

	private StorageFactory factory() {
    	return SubjectFixtureProvider.value();
    }

}
