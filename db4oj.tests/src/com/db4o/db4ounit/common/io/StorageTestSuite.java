package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.fixtures.*;

public class StorageTestSuite extends FixtureTestSuiteDescription {{

	fixtureProviders(
		new SubjectFixtureProvider(new Object[] {
    		new RandomAccessFileStorageFactory(),
    		new MemoryStorageFactory(),
    		new CachingStorageFactory(new RandomAccessFileStorageFactory()),
    	})
	);
	
	testUnits(
		StorageTest.class,
		ReadOnlyStorageTest.class,
		StorageFactoryTestUnit.class
	);
		
//	combinationToRun(2);
}}
