/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.Db4o;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.persistent.Atom;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class CascadeOnUpdate extends ClientServerTestCase {

	private static final int ATOM_COUNT = 10;

	private Atom child[];

	public void configure(Configuration config) {
		super.configure(config);
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
		Db4o.configure().objectClass(Atom.class).cascadeOnUpdate(true);
	}

	public void store(ExtObjectContainer oc) {
		CascadeOnUpdate cou = new CascadeOnUpdate();
		cou.child = new Atom[ATOM_COUNT];
		for (int i = 0; i < ATOM_COUNT; i++) {
			cou.child[i] = new Atom(new Atom("storedChild"), "stored");
		}
		oc.set(cou);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		CascadeOnUpdate cou = (CascadeOnUpdate) Db4oUtil.getOne(oc, this);
		for (int i = 0; i < ATOM_COUNT; i++) {
			cou.child[i].name = "updated" + seq;
			cou.child[i].child.name = "updated" + seq;
			oc.set(cou);
		}
	}

	public void check(ExtObjectContainer oc) {
		CascadeOnUpdate cou = (CascadeOnUpdate) Db4oUtil.getOne(oc, this);
		String name = cou.child[0].name;
		Assert.isTrue(name.startsWith("updated"));
		for (int i = 0; i < ATOM_COUNT; i++) {
			Assert.areEqual(name, cou.child[i].name);
			Assert.areEqual(name, cou.child[i].child.name);
		}
	}
}
