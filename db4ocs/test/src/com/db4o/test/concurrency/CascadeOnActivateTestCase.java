/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnActivateTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new CascadeOnActivateTestCase().runConcurrency();
	}
	
	public String name;

	public CascadeOnActivateTestCase child;

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnActivate(true);
	}

	protected void store() {
		name = "1";
		child = new CascadeOnActivateTestCase();
		child.name = "2";
		child.child = new CascadeOnActivateTestCase();
		child.child.name = "3";
		store(this);
	}

	public void conc(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(CascadeOnActivateTestCase.class);
		q.descend("name").constrain("1");
		ObjectSet os = q.execute();
		CascadeOnActivateTestCase coa = (CascadeOnActivateTestCase) os.next();
		CascadeOnActivateTestCase coa3 = coa.child.child;
		Assert.areEqual("3", coa3.name);
		oc.deactivate(coa, Integer.MAX_VALUE);
		Assert.isNull(coa3.name);
		oc.activate(coa, 1);
		Assert.areEqual("3", coa3.name);
	}
}
