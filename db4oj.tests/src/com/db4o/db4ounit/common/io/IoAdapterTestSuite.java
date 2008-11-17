package com.db4o.db4ounit.common.io;

import com.db4o.internal.caching.*;
import com.db4o.io.*;

import db4ounit.fixtures.*;

public class IoAdapterTestSuite extends FixtureTestSuiteDescription {{

	fixtureProviders(
		new SubjectFixtureProvider(new Object[] {
    		new RandomAccessFileAdapter(),
    		new CachedIoAdapter(new RandomAccessFileAdapter()),
    		new IoAdapterWithCache(new RandomAccessFileAdapter()) {
    			@Override
    			protected Cache4 newCache(int pageCount) {
    				return CacheFactory.new2QCache(pageCount);
    			}
    		}
    	})
	);
	
	testUnits(
		IoAdapterTest.class,
		ReadOnlyIoAdapterTest.class
	);
		
//	combinationToRun(2);
}}
