/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

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
		public final Procedure4<Object> _activateClosure;
		public final Procedure4<Object> _updateClosure;

		public CollectionSpec(Class<? extends CollectionClient> clientClass, Procedure4<Object> activateClosure, Procedure4<Object> updateClosure) {
			_clientClass = clientClass;
			_activateClosure = activateClosure;
			_updateClosure = updateClosure;
		}

		public String label() {
			return ReflectPlatform.simpleName(_clientClass);
		}
	}

	private static final Procedure4<Object> COLLECTION_ACTIVATE_CLOSURE = new Procedure4<Object>() {
		public void apply(Object arg) {
			((Collection)arg).iterator();
		}
	};
	private static final Procedure4<Object> COLLECTION_UPDATE_CLOSURE = new Procedure4<Object>() {
		public void apply(Object arg) {
			((Collection)arg).add("foo");
		}
	};
	
	private static final Procedure4<Object> MAP_ACTIVATE_CLOSURE = new Procedure4<Object>() {
		public void apply(Object arg) {
			((Map)arg).keySet();
		}
	};
	
	private static final Procedure4<Object> MAP_UPDATE_CLOSURE = new Procedure4<Object>() {
		public void apply(Object arg) {
			((Map)arg).put("foo", "bar");
		}
	};

	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new SimpleFixtureProvider(COLLECTION_SPEC,
				collectionSpec(ArrayListClient.class),
				mapSpec(HashMapClient.class),
				mapSpec(HashtableClient.class),
				collectionSpec(LinkedListClient.class),
				collectionSpec(StackClient.class),
				collectionSpec(HashSetClient.class)
			),
		};
	}

	private CollectionSpec collectionSpec(Class<? extends CollectionClient> clientClass) {
		return new CollectionSpec(clientClass, COLLECTION_ACTIVATE_CLOSURE, COLLECTION_UPDATE_CLOSURE);
	}

	private CollectionSpec mapSpec(Class<? extends CollectionClient> clientClass) {
		return new CollectionSpec(clientClass, MAP_ACTIVATE_CLOSURE, MAP_UPDATE_CLOSURE);
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
		
		private void assertActivatorInvocations(CollectionSpec collectionSpec) throws Exception {
			enhance();
			assertActivation(collectionSpec);
			assertUpdate(collectionSpec);
		}

		private void assertActivation(CollectionSpec collectionSpec) throws Exception {
			final Object collection = retrieveCollectionMember(collectionSpec);
			MockActivator collectionActivator = MockActivator.activatorFor((Activatable)collection);
			collectionSpec._activateClosure.apply(collection);
			assertReadsWrites(1, 0, collectionActivator);
		}

		private void assertUpdate(CollectionSpec collectionSpec) throws Exception {
			final Object collection = retrieveCollectionMember(collectionSpec);
			MockActivator collectionActivator = MockActivator.activatorFor((Activatable)collection);
			collectionSpec._updateClosure.apply(collection);
			assertReadsWrites(0, 1, collectionActivator);
		}

		private Object retrieveCollectionMember(CollectionSpec collectionSpec) throws Exception {
			AssertingClassLoader loader = newAssertingClassLoader(new Class[] { CollectionClient.class });		
			CollectionClient client = (CollectionClient) loader.newInstance(collectionSpec._clientClass);
			MockActivator clientActivator = MockActivator.activatorFor((Activatable)client);
			final Object collection = client.collectionInstance();
			assertReadsWrites(1, 0, clientActivator);
			return collection;
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
