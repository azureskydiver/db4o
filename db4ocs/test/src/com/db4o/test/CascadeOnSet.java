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

	public void concNoAccidentalDeletes(ExtObjectContainer oc) {
		noAccidentalDeletes1(oc, true, true);
		noAccidentalDeletes1(oc, true, false);
		noAccidentalDeletes1(oc, false, true);
		noAccidentalDeletes1(oc, false, false);
	}

	public void checkNoAccidentalDeletes(ExtObjectContainer oc) {
		Query query = oc.query();
		// FIXME: The assertion fails without following constration and running
		// together with other tests
		query.constrain(CascadeOnSet.class);
		query.descend("name").constrain("child.child");
		ObjectSet os = query.execute();
		// FIXME: The following assertion fails, os.size() returns randomly 30,31,32
		Assert.areEqual(TestConfigure.CONCURRENCY_THREAD_COUNT * 4, os.size());
	}

	private void noAccidentalDeletes1(ExtObjectContainer oc,
			boolean cascadeOnUpdate, boolean cascadeOnDelete) {
		oc.configure().objectClass(this).cascadeOnUpdate(cascadeOnUpdate);
		oc.configure().objectClass(this).cascadeOnDelete(cascadeOnDelete);

		name = "father";
		child = new CascadeOnSet();
		child.name = "child";
		child.child = new CascadeOnSet();
		child.child.name = "child.child";
		oc.set(this);
		if (!cascadeOnDelete && !cascadeOnUpdate) {
			// the only case, where we don't cascade
			oc.set(child.child);
		}

	}
}
