/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class DeleteDeep extends ClientServerTestCase {

	public String name;

	public DeleteDeep child;

	public void store(ExtObjectContainer oc) {
		addNodes(10);
		name = "root";
		oc.set(this);
	}

	private void addNodes(int count) {
		if (count > 0) {
			child = new DeleteDeep();
			child.name = "" + count;
			child.addNodes(count - 1);
		}
	}

	public void conc(ExtObjectContainer oc) throws Exception {
		oc.configure().objectClass(DeleteDeep.class).cascadeOnDelete(true);
		oc.configure().objectClass(DeleteDeep.class).cascadeOnActivate(true);
		Query q = oc.query();
		q.constrain(DeleteDeep.class);
		q.descend("name").constrain("root");
		ObjectSet os = q.execute();
		if (os.size() == 0) { // already deleted
			return;
		}
		Assert.areEqual(1, os.size());
		DeleteDeep root = (DeleteDeep) os.next();
		// wait for other threads
		Thread.sleep(500);
		oc.delete(root);
		// FIXME: the following assertion fails, but the same assertion in the
		// check method could pass
		Db4oUtil.assertOccurrences(oc, DeleteDeep.class, 0);
	}

	public void check(ExtObjectContainer oc) {
		Db4oUtil.assertOccurrences(oc, DeleteDeep.class, 0);
	}

}
