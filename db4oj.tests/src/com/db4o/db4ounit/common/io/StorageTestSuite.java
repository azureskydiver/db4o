package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.fixtures.*;

@SuppressWarnings("deprecation")
public class StorageTestSuite extends FixtureTestSuiteDescription {{

	fixtureProviders(
		new EnvironmentProvider(),
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
