package com.db4o.db4ounit.common.caching;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.caching.*;

import db4ounit.fixtures.*;

public class CacheTestSuite extends FixtureTestSuiteDescription {
	
	// initializer
	{
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
			},
			new Deferred4() {
				public Object value() {
					return CacheFactory.newLRUIntCache(10);
				}
			}
			
			// The following doesn' sharpen. Ignore for now.
			
//			,new Deferred4() {
//				public Object value() {
//					return new Cache4() {
//						
//						private final Cache4 _delegate = CacheFactory.newLRULongCache(10); 
//	
//						public Object produce(Object key, final Function4 producer, Procedure4 finalizer) {
//							Function4 delegateProducer = new Function4<Long, Object>() {
//								public Object apply(Long arg) {
//									return producer.apply(arg.intValue());
//								}
//							};
//							return _delegate.produce(((Integer)key).longValue(), delegateProducer, finalizer);
//						}
//	
//						public Iterator iterator() {
//							return _delegate.iterator();
//						}
//					};
//				}
//			}
		));
		
		testUnits(CacheTestUnit.class);
	}
	
}
