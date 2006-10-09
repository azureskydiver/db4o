/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */
package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.test.config.TestConfigure;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class MultiLevelIndex extends ClientServerTestCase {

	public MultiLevelIndex _child;

	public int _i;

	public int _level;

	public void configure(Configuration config) {
		config.objectClass(this).objectField("_child").indexed(true);
		config.objectClass(this).objectField("_i").indexed(true);
	}

	public void store(ExtObjectContainer oc) {
		store1(oc, 3);
		store1(oc, 2);
		store1(oc, 5);
		store1(oc, 1);
		for (int i = 6; i < 103; i++) {
			store1(oc, i);
		}
	}

	private void store1(ExtObjectContainer oc, int val) {
		MultiLevelIndex root = new MultiLevelIndex();
		root._i = val;
		root._child = new MultiLevelIndex();
		root._child._level = 1;
		root._child._i = -val;
		oc.set(root);
	}

	public void conc1(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(MultiLevelIndex.class);
		q.descend("_child").descend("_i").constrain(new Integer(-102));
		ObjectSet objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		MultiLevelIndex mli = (MultiLevelIndex) objectSet.next();
		Assert.areEqual(102, mli._i);
	}

	public void conc2(ExtObjectContainer oc, int seq) {
		oc.configure().objectClass(MultiLevelIndex.class).cascadeOnUpdate(true);
		Query q = oc.query();
		q.constrain(MultiLevelIndex.class);
		q.descend("_child").descend("_i").constrain(new Integer(seq - 102));
		ObjectSet objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		MultiLevelIndex mli = (MultiLevelIndex) objectSet.next();
		Assert.areEqual(102 - seq, mli._i);
		mli._child._i = -(seq + 201);
		oc.set(mli);
	}

	public void check2(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(MultiLevelIndex.class);
		q.descend("_child").descend("_i").constrain(new Integer(-200))
				.smaller();
		ObjectSet objectSet = q.execute();
		Assert.areEqual(TestConfigure.CONCURRENCY_THREAD_COUNT, objectSet
				.size());
	}

}
