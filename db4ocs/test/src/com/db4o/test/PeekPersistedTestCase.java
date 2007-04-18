/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class PeekPersistedTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new PeekPersistedTestCase().runConcurrency();
	}
	
	public String name;

	public PeekPersistedTestCase child;

	public void store() {
		PeekPersistedTestCase current = this;
		current.name = "1";
		for (int i = 2; i < 11; i++) {
			current.child = new PeekPersistedTestCase();
			current.child.name = "" + i;
			current = current.child;
		}
		store(this);
	}

	public void conc(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(PeekPersistedTestCase.class);
		q.descend("name").constrain("1");
		ObjectSet objectSet = q.execute();
		PeekPersistedTestCase pp = (PeekPersistedTestCase) objectSet.next();
		for (int i = 0; i < 10; i++) {
			peek(oc, pp, i);
		}
	}

	private void peek(ExtObjectContainer oc, PeekPersistedTestCase original, int depth) {
		PeekPersistedTestCase peeked = (PeekPersistedTestCase) oc.peekPersisted(original,
				depth, true);
		for (int i = 0; i <= depth; i++) {
			Assert.isNotNull(peeked);
			Assert.isFalse(oc.isStored(peeked));
			peeked = peeked.child;
		}
		Assert.isNull(peeked);
	}

}
