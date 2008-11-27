/* Copyright (C) 2008   db4objects Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.events;

import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class ExceptionPropagationInEventsTestSuite extends FixtureBasedTestSuite {

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] 
		                           {
										new Db4oFixtureProvider(),
										ExceptionPropagationInEventsTestVariables.EventProvider,
		                           };
	}

	public Class[] testUnits() {
		return new Class[] { ExceptionPropagationInEventsTestUnit.class };
	}
	
}