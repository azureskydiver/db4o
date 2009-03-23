/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

import java.net.*;
import java.util.*;

import com.db4o.db4ounit.common.ta.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.test.data.*;

import db4ounit.fixtures.*;

public class TACollectionFileEnhancerTestSuite  extends FixtureBasedTestSuite {

	private static FixtureVariable<CollectionSpec> COLLECTION_SPEC = new FixtureVariable<CollectionSpec>("coll");

	private static class CollectionSpec implements Labeled {
		public final Class<? extends CollectionClient> _clientClass;
		public final Procedure4<Object> _closure;
		public final int _expectedReads;
		public final int _expectedWrites;

		public CollectionSpec(Class<? extends CollectionClient> clientClass, int expectedReads, int expectedWrites, Procedure4<Object> closure) {
			_clientClass = clientClass;
			_closure = closure;
			_expectedReads = expectedReads;
			_expectedWrites = expectedWrites;
		}

		public String label() {
			return ReflectPlatform.simpleName(_clientClass) + "/" + (_expectedReads > 0 ? "R" : "W");
		}
	}
	
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new SimpleFixtureProvider(COLLECTION_SPEC,
				new CollectionSpec(ArrayListClient.class, 1, 0, new Procedure4<Object>() {
					public void apply(Object arg) {
						((List)arg).iterator();
					}
				}),
				new CollectionSpec(ArrayListClient.class, 0, 1, new Procedure4<Object>() {
					public void apply(Object arg) {
						((List)arg).add("foo");
					}
				}),
				new CollectionSpec(HashMapClient.class, 1, 0, new Procedure4<Object>() {
					public void apply(Object arg) {
						((Map)arg).keySet();
					}
				}),
				new CollectionSpec(HashMapClient.class, 0, 1, new Procedure4<Object>() {
					public void apply(Object arg) {
						((Map)arg).put("foo", "bar");
					}
				})
			),
		};
	}

	public Class[] testUnits() {
		return new Class[] {
			TACollectionFileEnhancerTestCase.class,	
		};
	}
	
	public static class TACollectionFileEnhancerTestCase extends TAFileEnhancerTestCaseBase {

		public void testActivatorInvocations() throws Exception {
			assertActivatorInvocations(collectionSpec());
		}
		
		private void assertActivatorInvocations(CollectionSpec collectionSpec) throws Exception,
				MalformedURLException, InstantiationException,
				IllegalAccessException, ClassNotFoundException {
			enhance();
			AssertingClassLoader loader = newAssertingClassLoader(new Class[] { CollectionClient.class });		
			CollectionClient client = (CollectionClient) loader.newInstance(collectionSpec._clientClass);
			MockActivator clientActivator = MockActivator.activatorFor((Activatable)client);
			final Object collection = client.collectionInstance();
			assertReadsWrites(1, 0, clientActivator);
			MockActivator collectionActivator = MockActivator.activatorFor((Activatable)collection);
			collectionSpec._closure.apply(collection);
			assertReadsWrites(collectionSpec._expectedReads, collectionSpec._expectedWrites, collectionActivator);
		}

		protected Class[] inputClasses() {
			return clientClassAsArray();
		}

		protected Class[] instrumentedClasses() {
			return clientClassAsArray();
		}
		
		private Class[] clientClassAsArray() {
			return new Class[] {
				collectionSpec()._clientClass,
			};
		}
		
		private CollectionSpec collectionSpec() {
			return COLLECTION_SPEC.value();
		}
	}

}
