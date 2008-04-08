/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import com.db4o.query.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

@SuppressWarnings("unchecked")
public class ArrayListTypeHandlerGreaterSmallerTestSuite extends FixtureBasedTestSuite implements Db4oTestCase {
	
	public FixtureProvider[] fixtureProviders() {
		ArrayListTypeHandlerTestElementsSpec[] elementSpecs = {
				ArrayListTypeHandlerTestVariables.STRING_ELEMENTS_SPEC,
				ArrayListTypeHandlerTestVariables.INT_ELEMENTS_SPEC,
		};
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			ArrayListTypeHandlerTestVariables.LIST_FIXTURE_PROVIDER,
			new SimpleFixtureProvider(
				ArrayListTypeHandlerTestVariables.ELEMENTS_SPEC,
				elementSpecs
			),
		};
	}

	public Class[] testUnits() { 
		return new Class[] {
			ArrayListTypeHandlerGreaterSmallerTestUnit.class,
		};
	}

	public static class ArrayListTypeHandlerGreaterSmallerTestUnit extends ArrayListTypeHandlerTestUnitBase {
		
		public void testSuccessfulSmallerQuery() throws Exception {
	    	Query q = newQuery(itemFactory().itemClass());
	    	q.descend(ItemFactory.LIST_FIELD_NAME).constrain(largeElement()).smaller();
	    	assertQueryResult(q, true);
		}
		
		public void testFailingGreaterQuery() throws Exception {
	    	Query q = newQuery(itemFactory().itemClass());
	    	q.descend(ItemFactory.LIST_FIELD_NAME).constrain(largeElement()).greater();
	    	assertQueryResult(q, false);
		}

	}

}
