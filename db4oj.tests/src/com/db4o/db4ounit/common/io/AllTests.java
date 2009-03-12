package com.db4o.db4ounit.common.io;

import db4ounit.*;


public class AllTests extends ReflectionTestSuite {
	
	public static void main(String[] arguments) {
		new ConsoleTestRunner(AllTests.class).run();
	}

	protected Class[] testCases() {
		return new Class[] {
			BlockAwareBinTestSuite.class,
			BlockSizeDependentBinTestCase.class,
			DiskFullTestCase.class,
			IoAdapterTestSuite.class,
			RandomAccessFileFactoryTestCase.class,
			MemoryBinGrowthTestCase.class,
			MemoryBinIsReusableTestCase.class,
			MemoryIoAdapterTestCase.class,
			NonFlushingStorageTestCase.class,
			RandomAccessFileStorageFactoryTestCase.class,
			StorageTestSuite.class,
			StackBasedDiskFullTestCase.class,
			
		};
	}

}
