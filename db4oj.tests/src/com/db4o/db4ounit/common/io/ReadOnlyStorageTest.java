package com.db4o.db4ounit.common.io;

import com.db4o.ext.*;
import com.db4o.io.*;

import db4ounit.*;

public class ReadOnlyStorageTest extends StorageTestUnitBase {
	
	public void test() {
		reopenAsReadOnly();
		assertReadOnly(_storage);
	}

	private void reopenAsReadOnly() {
	    close();
		open(true);
    }
	
	private void assertReadOnly(final StorageFactory.Bin adapter) {
		Assert.expect(Db4oIOException.class, new CodeBlock() {
			public void run() throws Throwable {
				adapter.write(0, new byte[] {0}, 1);
			}
		});
	}
}
