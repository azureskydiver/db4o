/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import com.db4o.query.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

@SuppressWarnings("unchecked")
public class ArrayListTypeHandlerStringElementTestSuite extends FixtureBasedTestSuite implements Db4oTestCase {
	
	
	public FixtureProvider[] fixtureProviders() {
		ArrayListTypeHandlerTestElementsSpec[] elementSpecs = {
				ArrayListTypeHandlerTestVariables.STRING_ELEMENTS_SPEC,
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
			ArrayListTypeHandlerStringElementTestUnit.class,
		};
	}

	public static class ArrayListTypeHandlerStringElementTestUnit extends ArrayListTypeHandlerTestUnitBase {
		
		public void testSuccessfulEndsWithQuery() throws Exception {
	    	Query q = newQuery(Item.class);
	    	q.descend("list").constrain(successfulEndChar()).endsWith(false);
	    	assertQueryResult(q, true);
		}
		
		public void testFailingEndsWithQuery() throws Exception {
	    	Query q = newQuery(Item.class);
	    	q.descend("list").constrain(failingEndChar()).endsWith(false);
	    	assertQueryResult(q, false);
		}

		private String successfulEndChar() {
			return String.valueOf(endChar());
		}

		private String failingEndChar() {
			return String.valueOf(endChar() + 1);
		}

		private char endChar() {
			String str = (String)elements()[0];
			return str.charAt(str.length()-1);
		}
	}

}
