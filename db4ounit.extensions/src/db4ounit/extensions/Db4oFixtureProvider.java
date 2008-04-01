/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions;

import db4ounit.fixtures.*;

public class Db4oFixtureProvider extends SimpleFixtureProvider {

	public Db4oFixtureProvider() {
		super("db4o", AbstractDb4oTestCase.FIXTURE_VARIABLE, AbstractDb4oTestCase.FIXTURE_VARIABLE.value());
	}
	
}
