package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.*;

public class MemoryIoAdapterTestCase implements TestCase {

	private static final String URL = "url";
	private static final int GROW_BY = 100;

	public void testGrowth() {
		MemoryIoAdapter factory = new MemoryIoAdapter();
		factory.growBy(GROW_BY);
		IoAdapter io = factory.open(URL, false, 0, false);
		assertLength(factory, 0);
		writeBytes(io, GROW_BY - 1);
		assertLength(factory, GROW_BY);
		writeBytes(io, GROW_BY - 1);
		assertLength(factory, GROW_BY * 2);
		writeBytes(io, GROW_BY * 2);
		assertLength(factory, GROW_BY * 4 - 2);
	}

	private void writeBytes(IoAdapter io, int numBytes) {
		io.write(new byte[numBytes]);
	}

	private void assertLength(MemoryIoAdapter factory, int expected) {
		Assert.areEqual(expected, factory.get(URL).length);
	}
	
}
