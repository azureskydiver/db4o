package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.fixtures.*;

public class IoAdapterTestSuite extends FixtureBasedTestSuite {

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new SubjectFixtureProvider(new Object[] {
				new RandomAccessFileAdapter(),
				new CachedIoAdapter(new RandomAccessFileAdapter()),
//				new IoAdapterWithCache(new RandomAccessFileAdapter()) {
//					@Override
//					protected Cache4 newCache() {
//						return CacheFactory.new2QCache(64);
//					}
//				}
			}),
		};
	}
	
//	@Override
//	public int[] combinationToRun() {
//		return new int[] { 2 };
//	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
			IoAdapterTest.class,
			ReadOnlyIoAdapterTest.class,
		};
	}

}
