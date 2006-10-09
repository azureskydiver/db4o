/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.Enumeration;
import java.util.Vector;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.persistent.Atom;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class CascadeToVector extends ClientServerTestCase {

	public Vector vec;

	public void configure(Configuration config) {
		config.objectClass(this).cascadeOnUpdate(true);
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(Atom.class).cascadeOnDelete(false);
	}

	public void store(ExtObjectContainer oc) {
		CascadeToVector ctv = new CascadeToVector();
		ctv.vec = new Vector();
		ctv.vec.addElement(new Atom("stored1"));
		ctv.vec.addElement(new Atom(new Atom("storedChild1"), "stored2"));
		oc.set(ctv);
	}

	public void conc(ExtObjectContainer oc) {
		CascadeToVector ctv = (CascadeToVector) Db4oUtil.getOne(oc,
				CascadeToVector.class);
		Enumeration i = ctv.vec.elements();
		while (i.hasMoreElements()) {
			Atom atom = (Atom) i.nextElement();
			atom.name = "updated";
			if (atom.child != null) {
				// This one should NOT cascade
				atom.child.name = "updated";
			}
		}
		oc.set(ctv);
	}

	public void check(ExtObjectContainer oc) {

		CascadeToVector ctv = (CascadeToVector) Db4oUtil.getOne(oc,
				CascadeToVector.class);
		Enumeration i = ctv.vec.elements();
		while (i.hasMoreElements()) {
			Atom atom = (Atom) i.nextElement();
			Assert.areEqual("updated", atom.name);
			if (atom.child != null) {
				Assert.areEqual("storedChild1", atom.child.name);
			}
		}
	}

	public void concDelete(ExtObjectContainer oc, int seq) throws Exception {
		ObjectSet os = oc.query(CascadeToVector.class);
		if (os.size() == 0) { // already deleted
			return;
		}
		Assert.areEqual(1, os.size());
		CascadeToVector ctv = (CascadeToVector) os.next();
		// wait for other threads
		Thread.sleep(500);
		oc.delete(ctv);
	}

	public void checkDelete(ExtObjectContainer oc) {
		// Cascade-On-Delete Test: We only want one atom to remain.
		Db4oUtil.assertOccurrences(oc, Atom.class, 1);
	}
}
