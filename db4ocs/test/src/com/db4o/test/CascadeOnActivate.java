/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnActivate extends AbstractDb4oTestCase {

	public String name;

	public CascadeOnActivate child;

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnActivate(true);
	}

	public void store(ExtObjectContainer oc) {
		name = "1";
		child = new CascadeOnActivate();
		child.name = "2";
		child.child = new CascadeOnActivate();
		child.child.name = "3";
		oc.set(this);
	}

	public void conc(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(CascadeOnActivate.class);
		q.descend("name").constrain("1");
		ObjectSet os = q.execute();
		CascadeOnActivate coa = (CascadeOnActivate) os.next();
		CascadeOnActivate coa3 = coa.child.child;
		Assert.areEqual("3", coa3.name);
		oc.deactivate(coa, Integer.MAX_VALUE);
		Assert.isNull(coa3.name);
		oc.activate(coa, 1);
		Assert.areEqual("3", coa3.name);
	}

	public void concIndexed(ExtObjectContainer oc) {
		oc.configure().objectClass(CascadeOnActivate.class).objectField("name")
				.indexed(true);
		conc(oc);
	}

}
