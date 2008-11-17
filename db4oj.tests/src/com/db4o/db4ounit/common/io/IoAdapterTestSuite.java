package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.fixtures.*;

public class IoAdapterTestSuite extends FixtureBasedTestSuite {

	/**
	 * @decaf.replaceFirst return jdk11FixtureProviders();
	 */
	@Override
	public FixtureProvider[] fixtureProviders() {
		return allFixtureProviders();
	}
	
	@SuppressWarnings("unused")
    private FixtureProvider[] jdk11FixtureProviders() {
	    return new FixtureProvider[] {
			new SubjectFixtureProvider(new Object[] {
				new RandomAccessFileAdapter(),
				new CachedIoAdapter(new RandomAccessFileAdapter()),
			}),
		};
    }

	/**
	 * @decaf.ignore
	 */
	private FixtureProvider[] allFixtureProviders() {
	    return new FixtureProvider[] {
			new SubjectFixtureProvider(new Object[] {
				new RandomAccessFileAdapter(),
				new CachedIoAdapter(new RandomAccessFileAdapter()),
				new IoAdapterWithCache(new RandomAccessFileAdapter()) {
					@Override
					protected com.db4o.internal.caching.Cache4 newCache(int pageCount) {
						return com.db4o.internal.caching.CacheFactory.newLRUCache(pageCount);
					}
				}
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
