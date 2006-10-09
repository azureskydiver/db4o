/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class PeekPersisted extends ClientServerTestCase {

	public String name;

	public PeekPersisted child;

	public void store(ExtObjectContainer oc) {
		PeekPersisted current = this;
		current.name = "1";
		for (int i = 2; i < 11; i++) {
			current.child = new PeekPersisted();
			current.child.name = "" + i;
			current = current.child;
		}
		oc.set(this);
	}

	public void conc(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(PeekPersisted.class);
		q.descend("name").constrain("1");
		ObjectSet objectSet = q.execute();
		PeekPersisted pp = (PeekPersisted) objectSet.next();
		for (int i = 0; i < 10; i++) {
			peek(oc, pp, i);
		}
	}

	private void peek(ExtObjectContainer oc, PeekPersisted original, int depth) {
		PeekPersisted peeked = (PeekPersisted) oc.peekPersisted(original,
				depth, true);
		for (int i = 0; i <= depth; i++) {
			Assert.isNotNull(peeked);
			Assert.isFalse(oc.isStored(peeked));
			peeked = peeked.child;
		}
		Assert.isNull(peeked);
	}

}
