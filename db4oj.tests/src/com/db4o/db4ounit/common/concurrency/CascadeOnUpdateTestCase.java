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

	public static class Item {
		public Atom[] child;
	}

	protected void configure(Configuration config) {
		config.objectClass(Item.class).cascadeOnUpdate(true);
		config.objectClass(Atom.class).cascadeOnUpdate(true);
	}

	protected void store() {
		Item item = new Item();
		item.child = new Atom[ATOM_COUNT];
		for (int i = 0; i < ATOM_COUNT; i++) {
			item.child[i] = new Atom(new Atom("storedChild"), "stored");
		}
		store(item);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		Item item = (Item) retrieveOnlyInstance(oc, Item.class);
		for (int i = 0; i < ATOM_COUNT; i++) {
			item.child[i].name = "updated" + seq;
			item.child[i].child.name = "updated" + seq;
			oc.set(item);
		}
	}

	public void check(ExtObjectContainer oc) {
		Item item = (Item) retrieveOnlyInstance(Item.class);
		String name = item.child[0].name;
		Assert.isTrue(name.startsWith("updated"));
		for (int i = 0; i < ATOM_COUNT; i++) {
			Assert.areEqual(name, item.child[i].name);
			Assert.areEqual(name, item.child[i].child.name);
		}
	}
}
