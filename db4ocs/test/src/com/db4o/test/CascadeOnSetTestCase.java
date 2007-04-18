/* Copyright (C) 2004 - 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnSetTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new CascadeOnSetTestCase().runConcurrency();
	}

	public String name;

	public CascadeOnSetTestCase child;

	public static CascadeOnSetTestCase sameInstance = new CascadeOnSetTestCase();

	public void conc(ExtObjectContainer oc) {
		store(oc, true, true);
		store(oc, true, false);
		store(oc, false, true);
		store(oc, false, false);
	}

	public void check(ExtObjectContainer oc) {
		Query query = oc.query();
		// FIXME: the query fails without query.constrain(CascadeOnSet.class)
		// query.constrain(CascadeOnSet.class);
		query.descend("name").constrain("child.child");
		ObjectSet os = query.execute();
		Assert.areEqual(threadCount() * 4, os.size());
	}

	private void store(ExtObjectContainer oc, boolean cascadeOnUpdate,
			boolean cascadeOnDelete) {
		oc.configure().objectClass(this).cascadeOnUpdate(cascadeOnUpdate);
		oc.configure().objectClass(this).cascadeOnDelete(cascadeOnDelete);
		CascadeOnSetTestCase cos = new CascadeOnSetTestCase();
		cos.name = "father";
		cos.child = new CascadeOnSetTestCase();
		cos.child.name = "child";
		cos.child.child = new CascadeOnSetTestCase();
		cos.child.child.name = "child.child";
		oc.set(cos);
		if (!cascadeOnDelete && !cascadeOnUpdate) {
			// the only case, where we don't cascade
			oc.set(cos.child.child);
		}
	}
}
