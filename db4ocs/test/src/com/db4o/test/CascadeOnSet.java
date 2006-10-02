/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.Db4o;
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
		noAccidentalDeletes1(true, true);
		noAccidentalDeletes1(true, false);
		noAccidentalDeletes1(false, true);
		noAccidentalDeletes1(false, false);
	}

	public void checkNoAccidentalDeletes(ExtObjectContainer oc) {
		Query query = oc.query();
		query.descend("name").constrain("child.child");
		ObjectSet os = query.execute();
		// FIXME: following assertion fails when executing whole test suites.
		Assert.areEqual(TestConfigure.CONCURRENCY_THREAD_COUNT * 4, os.size());
	}

	private void noAccidentalDeletes1(boolean cascadeOnUpdate,
			boolean cascadeOnDelete) {
		Db4o.configure().objectClass(this).cascadeOnUpdate(cascadeOnUpdate);
		Db4o.configure().objectClass(this).cascadeOnDelete(cascadeOnDelete);
		ExtObjectContainer oc = db();
		try {
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
		} finally {
			oc.close();
		}
	}
}
