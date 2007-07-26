/* Copyright (C) 2004 - 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.concurrency;

import com.db4o.config.*;
import com.db4o.db4ounit.common.persistent.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnUpdateTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new CascadeOnUpdateTestCase().runConcurrency();
	}
	
	private static final int ATOM_COUNT = 10;

	public Atom[] child;

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnUpdate(true);
		config.objectClass(Atom.class).cascadeOnUpdate(true);
	}

	protected void store() {
		CascadeOnUpdateTestCase cou = new CascadeOnUpdateTestCase();
		cou.child = new Atom[ATOM_COUNT];
		for (int i = 0; i < ATOM_COUNT; i++) {
			cou.child[i] = new Atom(new Atom("storedChild"), "stored");
		}
		store(cou);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		CascadeOnUpdateTestCase cou = (CascadeOnUpdateTestCase) retrieveOnlyInstance(oc, this.getClass());
		for (int i = 0; i < ATOM_COUNT; i++) {
			cou.child[i].name = "updated" + seq;
			cou.child[i].child.name = "updated" + seq;
			oc.set(cou);
		}
	}

	public void check(ExtObjectContainer oc) {
		CascadeOnUpdateTestCase cou = (CascadeOnUpdateTestCase) retrieveOnlyInstance(CascadeOnUpdateTestCase.class);
		String name = cou.child[0].name;
		Assert.isTrue(name.startsWith("updated"));
		for (int i = 0; i < ATOM_COUNT; i++) {
			Assert.areEqual(name, cou.child[i].name);
			Assert.areEqual(name, cou.child[i].child.name);
		}
	}
}
