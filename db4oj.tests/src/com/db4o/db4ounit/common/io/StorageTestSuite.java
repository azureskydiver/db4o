/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */
package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.partial
 */
@SuppressWarnings("deprecation")
public class StorageTestSuite extends FixtureTestSuiteDescription {

	/**
	 * @sharpen.ignore
	 */
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new EnvironmentProvider(),
				new SubjectFixtureProvider(new Object[] {
						TestPlatform.newPersistentStorage(),
						new MemoryStorage(),
						new CachingStorage(TestPlatform.newPersistentStorage()),
						new IoAdapterStorage(new RandomAccessFileAdapter()),
				})			
		};
	}
	
	
	@Override
	public Class[] testUnits() {
		return new Class[] {
				BinTest.class,
				ReadOnlyBinTest.class,
				StorageTest.class				
		};
	}
		
//	combinationToRun(2);
}
