/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.config.*;
import com.db4o.cs.common.util.*;
import com.db4o.ext.*;
import com.db4o.test.persistent.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnUpdate2 extends AbstractDb4oTestCase {

	private static final int ATOM_COUNT = 10;

	private Atom child[];

	public void configure(Configuration config) {
		config.objectClass(this).cascadeOnUpdate(true);
		config.objectClass(Atom.class).cascadeOnUpdate(false);
	}

	public void store(ExtObjectContainer oc) {
		CascadeOnUpdate2 cou = new CascadeOnUpdate2();
		cou.child = new Atom[ATOM_COUNT];
		for (int i = 0; i < ATOM_COUNT; i++) {
			cou.child[i] = new Atom(new Atom("storedChild"), "stored");
		}
		oc.set(cou);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		CascadeOnUpdate2 cou = (CascadeOnUpdate2) Db4oUtil.getOne(oc, this);
		for (int i = 0; i < ATOM_COUNT; i++) {
			cou.child[i].name = "updated" + seq;
			cou.child[i].child.name = "updated" + seq;
			oc.set(cou);
		}
	}

	public void check(ExtObjectContainer oc) {
		CascadeOnUpdate2 cou = (CascadeOnUpdate2) Db4oUtil.getOne(oc, this);
		String name = cou.child[0].name;
		Assert.isTrue(name.startsWith("updated"));
		for (int i = 0; i < ATOM_COUNT; i++) {
			Assert.areEqual(name, cou.child[i].name);
			Assert.areEqual("storedChild", cou.child[i].child.name);
		}
	}

	public void concIndexed(ExtObjectContainer oc, int seq) {
		oc.configure().objectClass(Atom.class).objectField("name")
				.indexed(true);
		conc(oc, seq);
	}

	public void checkIndexed(ExtObjectContainer oc) {
		check(oc);
	}
}
