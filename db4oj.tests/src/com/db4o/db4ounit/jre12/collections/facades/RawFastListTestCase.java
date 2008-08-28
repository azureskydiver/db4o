/* Copyright (C) 2007 db4objects Inc. http://www.db4o.com */
package com.db4o.db4ounit.jre12.collections.facades;

import com.db4o.collections.facades.*;
import com.db4o.db4ounit.jre12.collections.*;

import db4ounit.extensions.fixtures.*;

/**
 * @decaf.ignore.jdk11
 */
public class RawFastListTestCase extends FastListTestCaseBase implements OptOutCS {
	
	public static void main(String[] args) {
		new RawFastListTestCase().runAll();
	}
	
	public RawFastListTestCase() {
		init(new FastList(new MockPersistentList()));
	}

}
