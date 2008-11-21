package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.fixtures.*;

public class IoAdapterTestSuite extends FixtureTestSuiteDescription {{

	fixtureProviders(
		new SubjectFixtureProvider(new Object[] {
    		new RandomAccessFileAdapter(),
    		new CachedIoAdapter(new RandomAccessFileAdapter()),
    	})
	);
	
	testUnits(
		IoAdapterTest.class,
		ReadOnlyIoAdapterTest.class
	);
		
//	combinationToRun(2);
}}
