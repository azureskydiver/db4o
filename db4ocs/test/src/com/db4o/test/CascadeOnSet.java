/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.test.config.TestConfigure;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class CascadeOnSet extends ClientServerTestCase {

	public String name;

	public CascadeOnSet child;

	public static CascadeOnSet sameInstance = new CascadeOnSet();

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
		Assert.areEqual(TestConfigure.CONCURRENCY_THREAD_COUNT * 4, os.size());
	}

	private void store(ExtObjectContainer oc, boolean cascadeOnUpdate,
			boolean cascadeOnDelete) {
		oc.configure().objectClass(this).cascadeOnUpdate(cascadeOnUpdate);
		oc.configure().objectClass(this).cascadeOnDelete(cascadeOnDelete);
		CascadeOnSet cos = new CascadeOnSet();
		cos.name = "father";
		cos.child = new CascadeOnSet();
		cos.child.name = "child";
		cos.child.child = new CascadeOnSet();
		cos.child.child.name = "child.child";
		oc.set(cos);
		if (!cascadeOnDelete && !cascadeOnUpdate) {
			// the only case, where we don't cascade
			oc.set(cos.child.child);
		}
	}
}
