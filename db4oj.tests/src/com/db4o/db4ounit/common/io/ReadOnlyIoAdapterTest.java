package com.db4o.db4ounit.common.io;

import com.db4o.ext.*;
import com.db4o.io.*;

import db4ounit.*;

public class ReadOnlyIoAdapterTest extends IoAdapterTestUnitBase {
	
	public void test() {
		reopenAsReadOnly();
		assertReadOnly(_adapter);
	}

	private void reopenAsReadOnly() {
	    close();
		open(true);
    }
	
	private void assertReadOnly(final IoAdapter adapter) {
		Assert.expect(Db4oIOException.class, new CodeBlock() {
			public void run() throws Throwable {
				adapter.write(new byte[] {0});
			}
		});
	}
}
