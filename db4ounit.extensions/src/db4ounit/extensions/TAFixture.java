/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.extensions.fixtures.*;

/**
 * Fixture that runs test cases with TransparentActivationSupport
 * against an InMemoryObjectContainer.
 * 
 * TODO: This shouldn't really be a fixture but some kind of
 * ConfigurationMixin that could be composed with any existing
 * fixture (we do want to run all the C/S test cases
 * in TA mode for instance).
 */
public class TAFixture extends Db4oInMemory {
	
	public boolean accept(Class clazz) {
		if (!Db4oTestCase.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (OptOutTA.class.isAssignableFrom(clazz)) {
			return false;
		}
		return super.accept(clazz);
	}
	
	public String getLabel() {
		return "TA";
	}
	
	protected ObjectContainer createDatabase(Configuration config) {
		config.add(new TransparentActivationSupport());
		return super.createDatabase(config);
	}
}
