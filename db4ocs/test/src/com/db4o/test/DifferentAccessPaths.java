/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class DifferentAccessPaths extends ClientServerTestCase {

	public String foo;

	public void store(ExtObjectContainer oc) {
		DifferentAccessPaths dap = new DifferentAccessPaths();
		dap.foo = "hi";
		oc.set(dap);
		dap = new DifferentAccessPaths();
		dap.foo = "hi too";
		oc.set(dap);
	}

	public void conc(ExtObjectContainer oc) throws Exception {
		DifferentAccessPaths dap = query(oc);
		for (int i = 0; i < 10; i++) {
			Assert.areSame(dap, query(oc));
		}
		oc.purge(dap);
		Assert.areNotSame(dap, query(oc));
	}

	private DifferentAccessPaths query(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(DifferentAccessPaths.class);
		q.descend("foo").constrain("hi");
		ObjectSet os = q.execute();
		Assert.areEqual(1, os.size());
		DifferentAccessPaths dap = (DifferentAccessPaths) os.next();
		return dap;
	}

}
