package com.db4o.db4ounit.common.io;

import com.db4o.db4ounit.common.util.*;

import db4ounit.*;


public class AllTests extends ReflectionTestSuite {
	
	public static void main(String[] arguments) {
		new ConsoleTestRunner(AllTests.class).run();
	}

	protected Class[] testCases() {
		return new Class[] {
			BlockAwareBinTestSuite.class,
			BlockSizeDependentBinTestCase.class,
			IoAdapterTestSuite.class,
			MemoryBinIsReusableTestCase.class,
			RandomAccessFileStorageFactoryTestCase.class,
			StorageTestSuite.class,
			NonFlushingStorageTestCase.class,
			DiskFullTestCase.class,
			StackBasedDiskFullTestCase.class,
		};
	}

}
