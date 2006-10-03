/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.Hashtable;

import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.persistent.Atom;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class CascadeToHashtable extends ClientServerTestCase {

	public Hashtable ht;

	public void configure(ExtObjectContainer oc) {
		oc.configure().objectClass(this).cascadeOnUpdate(true);
		oc.configure().objectClass(this).cascadeOnDelete(true);
		oc.configure().objectClass(Atom.class).cascadeOnDelete(false);
	}

	public void store(ExtObjectContainer oc) {
		CascadeToHashtable cth = new CascadeToHashtable();
		cth.ht = new Hashtable();
		cth.ht.put("key1", new Atom("stored1"));
		cth.ht.put("key2", new Atom(new Atom("storedChild1"), "stored2"));
		oc.set(cth);
	}

	public void conc(ExtObjectContainer oc) {
		CascadeToHashtable cth = (CascadeToHashtable) Db4oUtil.getOne(oc, this);
		cth.ht.put("key1", new Atom("updated1"));
		Atom atom = (Atom) cth.ht.get("key2");
		atom.name = "updated2";
		oc.set(cth);
	}

	public void check(ExtObjectContainer oc) {
		CascadeToHashtable cth = (CascadeToHashtable) Db4oUtil.getOne(oc, this);
		Atom atom = (Atom) cth.ht.get("key1");
		Assert.areEqual("updated1", atom.name);
		atom = (Atom) cth.ht.get("key2");
		Assert.areEqual("updated2", atom.name);
	}

	public void concDelete(ExtObjectContainer oc, int seq) throws Exception {
		ObjectSet os = oc.query(CascadeToHashtable.class);
		if (os.size() == 0) { // already deleted
			return;
		}
		Assert.areEqual(1, os.size());
		CascadeToHashtable cth = (CascadeToHashtable) os.next();
		// wait for other threads
		Thread.sleep(500);
		oc.delete(cth);
	}

	public void checkDelete(ExtObjectContainer oc) {
		// Cascade-On-Delete Test: We only want one atom to remain.
		Db4oUtil.assertOccurrences(oc, Atom.class, 1);
	}
}
