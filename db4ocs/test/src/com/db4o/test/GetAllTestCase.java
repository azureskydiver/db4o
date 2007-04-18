/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class GetAllTestCase extends Db4oClientServerTestCase {
	public static void main(String[] args) {
		new GetAllTestCase().runConcurrency();
	}

	public void store() {
		store(new GetAllTestCase());
		store(new GetAllTestCase());
	}

	public void conc(ExtObjectContainer oc) {
		Assert.areEqual(2, oc.get(null).size());
	}

	public void concSODA(ExtObjectContainer oc) {
		Assert.areEqual(2, oc.query().execute().size());
	}
}
