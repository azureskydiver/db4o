/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class GreaterOrEqual extends ClientServerTestCase {

	public int val;

	public GreaterOrEqual() {

	}

	public GreaterOrEqual(int val) {
		this.val = val;
	}

	public void store(ExtObjectContainer oc) {
		oc.set(new GreaterOrEqual(1));
		oc.set(new GreaterOrEqual(2));
		oc.set(new GreaterOrEqual(3));
		oc.set(new GreaterOrEqual(4));
		oc.set(new GreaterOrEqual(5));
	}

	public void conc(ExtObjectContainer oc) {
		int[] expect = { 3, 4, 5 };
		Query q = oc.query();
		q.constrain(GreaterOrEqual.class);
		q.descend("val").constrain(new Integer(3)).greater().equal();
		ObjectSet res = q.execute();
		while (res.hasNext()) {
			GreaterOrEqual r = (GreaterOrEqual) res.next();
			for (int i = 0; i < expect.length; i++) {
				if (expect[i] == r.val) {
					expect[i] = 0;
				}
			}
		}
		for (int i = 0; i < expect.length; i++) {
			Assert.areEqual(0, expect[i]);
		}
	}

}
