/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class GetAll extends AbstractDb4oTestCase {

	public void store(ExtObjectContainer oc) {
		oc.set(new GetAll());
		oc.set(new GetAll());
	}

	public void conc(ExtObjectContainer oc) {
		Assert.areEqual(2, oc.get(null).size());
	}

	public void concSODA(ExtObjectContainer oc) {
		Assert.areEqual(2, oc.query().execute().size());
	}

}
