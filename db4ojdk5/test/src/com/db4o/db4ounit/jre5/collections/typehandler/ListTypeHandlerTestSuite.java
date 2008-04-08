/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import com.db4o.query.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

@SuppressWarnings("unchecked")
public class ListTypeHandlerTestSuite extends FixtureBasedTestSuite implements Db4oTestCase {
	
	
	public FixtureProvider[] fixtureProviders() {
		ListTypeHandlerTestElementsSpec[] elementSpecs = {
				ListTypeHandlerTestVariables.STRING_ELEMENTS_SPEC,
				ListTypeHandlerTestVariables.INT_ELEMENTS_SPEC,
				ListTypeHandlerTestVariables.OBJECT_ELEMENTS_SPEC,
		};
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			ListTypeHandlerTestVariables.LIST_FIXTURE_PROVIDER,
			new SimpleFixtureProvider(
				ListTypeHandlerTestVariables.ELEMENTS_SPEC,
				elementSpecs
			),
		};
	}

	public Class[] testUnits() { 
		return new Class[] {
			ListTypeHandlerTestUnit.class,
		};
	}

	public static class ListTypeHandlerTestUnit extends ListTypeHandlerTestUnitBase {
		
	    public void testRetrieveInstance(){
	        Object item = retrieveOnlyInstance(itemFactory().itemClass());
	        assertListContent(item);
	    }
	    
	    public void testSuccessfulQuery() throws Exception {
	    	assertQuery(true, elements()[0]);
		}

	    public void testFailingQuery() throws Exception {
	    	assertQuery(false, notContained());
		}

		public void testCompareItems() throws Exception {
	    	assertCompareItems(elements()[0], true);
	    }

		public void testFailingCompareItems() throws Exception {
	    	assertCompareItems(notContained(), false);
	    }

		private void assertCompareItems(Object element, boolean successful) {
			Query q = newQuery();
	    	Object item = itemFactory().newItem();
	    	List list = listFromItem(item);
			list.add(element);
	    	q.constrain(item);
			assertQueryResult(q, successful);
		}

		private void assertQuery(boolean successful, Object element) {
			Query q = newQuery(itemFactory().itemClass());
			q.descend(ItemFactory.LIST_FIELD_NAME).constrain(element);
			assertQueryResult(q, successful);
		}

	}

}
