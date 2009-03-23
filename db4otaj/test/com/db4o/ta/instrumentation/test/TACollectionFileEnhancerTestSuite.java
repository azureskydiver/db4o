/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test;

import com.db4o.foundation.*;

import db4ounit.fixtures.*;

public class TACollectionFileEnhancerTestSuite  extends FixtureBasedTestSuite {

	private static FixtureVariable COLLECTION_SPEC = new FixtureVariable("coll");

	private static class CollectionSpec {
		public final Class _clientClass;
		public final Procedure4<Object> _activationClosure;
		public final Procedure4<Object> _persistenceClosure;

		public CollectionSpec(Class clientClass, Procedure4<Object> activationClosure, Procedure4<Object> persistenceClosure) {
			super();
			_clientClass = clientClass;
			_activationClosure = activationClosure;
			_persistenceClosure = persistenceClosure;
		}
	}
	
	public FixtureProvider[] fixtureProviders() {
		return null;
	}

	public Class[] testUnits() {
		return null;
	}
	
	private static class TACollectionFileEnhancerTestCase extends TAFileEnhancerTestCaseBase {

		protected Class[] inputClasses() {
			return null;
		}

		protected Class[] instrumentedClasses() {
			return null;
		}
		
	}

}
