package com.db4o.db4ounit.common.io;

import com.db4o.db4ounit.common.api.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class StorageTest extends TestWithTempFile {
	
	public void testInitialLength() {
		
		storage().open(new BinConfiguration(_tempFile, false, 1000, false)).close();
		
		final Bin bin = storage().open(new BinConfiguration(_tempFile, false, 0, false));
		try {
			Assert.areEqual(1000, bin.length());
		} finally {
			bin.close();
		}
		
	}

	private Storage storage() {
    	return SubjectFixtureProvider.value();
    }

}
