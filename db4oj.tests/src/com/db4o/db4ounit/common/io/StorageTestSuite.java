package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.fixtures.*;

public class StorageTestSuite extends FixtureTestSuiteDescription {{

	fixtureProviders(
		new SubjectFixtureProvider(new Object[] {
    		new FileStorage(),
    		new MemoryStorage(),
    		new CachingStorage(new FileStorage()),
    		new IoAdapterStorage(new RandomAccessFileAdapter()),
    	})
	);
	
	testUnits(
		BinTest.class,
		ReadOnlyBinTest.class,
		StorageTest.class
	);
		
//	combinationToRun(2);
}}
