/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class ArrayListTypeHandlerTestSuite extends FixtureBasedTestSuite implements Db4oTestCase {
	
	public FixtureProvider[] fixtureProviders() {
		ArrayListHandlerTestElementsSpec[] elementSpecs = {
				new ArrayListHandlerTestElementsSpec(new Object[]{ "zero", "one" }, "two"),
				new ArrayListHandlerTestElementsSpec(new Object[]{ new Integer(0), new Integer(1) }, new Integer(2)),
				new ArrayListHandlerTestElementsSpec(new Object[]{ new FirstClassElement(0), new FirstClassElement(2) }, new FirstClassElement(2)),
		};
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			new SimpleFixtureProvider(
				ArrayListHandlerTestVariables.LIST_IMPLEMENTATION,
				new Deferred4() {
					public Object value() {
						return new ArrayList();
					}
				}
			),
			new SimpleFixtureProvider(
				ArrayListHandlerTestVariables.ELEMENTS_SPEC,
				elementSpecs
			),
//			new SimpleFixtureProvider(
//				AbstractDb4oTestCase.FIXTURE_VARIABLE,
//				new Object[] {
//					new Db4oInMemory(),
//					new Db4oSolo(),
//					new Db4oClientServer(configSource(), true, "c/s emb"),
//					new Db4oClientServer(configSource(), false, "c/s net"),
//				}
//			),
		};
	}

	public Class[] testUnits() { 
		return new Class[] {
			ArrayListTypeHandlerTestUnit.class,
		};
	}

	private IndependentConfigurationSource configSource() {
		return new IndependentConfigurationSource();
	}
	
}
