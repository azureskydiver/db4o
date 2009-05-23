package com.db4o.db4ounit.common.io;

import db4ounit.*;
import db4ounit.extensions.*;


public class AllTests extends ComposibleReflectionTestSuite {
	
	public static void main(String[] arguments) {
		new ConsoleTestRunner(AllTests.class).run();
	}

	protected Class[] testCases() {
		return composeTests(new Class[] {
									BlockAwareBinTestSuite.class,
									DiskFullTestCase.class,
									MemoryBinGrowthTestCase.class,
									MemoryBinIsReusableTestCase.class,
									MemoryIoAdapterTestCase.class,
									NonFlushingStorageTestCase.class,
									RandomAccessFileStorageFactoryTestCase.class,
									StorageTestSuite.class,
									StackBasedDiskFullTestCase.class,
							});
	}
	

	/**
	 * @sharpen.if !SILVERLIGHT
	 */
	protected Class[] composeWith() {
		return new Class[] { 
						BlockSizeDependentBinTestCase.class, 
						IoAdapterTestSuite.class,
						RandomAccessFileFactoryTestCase.class,
				};
	}
}