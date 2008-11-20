package com.db4o.db4ounit.common.caching;

import com.db4o.internal.caching.*;

import db4ounit.fixtures.*;

public class CacheTestSuite extends FixtureTestSuiteDescription {{
	
	fixtureProviders(new SubjectFixtureProvider(
		new Deferred4() {
			public Object value() {
				return CacheFactory.newLRUCache(10);
			}
		},
		new Deferred4() {
			public Object value() {
				return CacheFactory.new2QCache(10);
			}
		},
		new Deferred4() {
			public Object value() {
				return CacheFactory.new2QXCache(10);
			}
		}
	));
	
	testUnits(CacheTestUnit.class);

}}
