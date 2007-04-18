/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test.concurrency;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.common.util.*;
import com.db4o.ext.*;
import com.db4o.test.persistent.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeToVectorTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new CascadeToVectorTestCase().runConcurrency();
	}

	public Vector vec;

	public void configure(Configuration config) {
		config.objectClass(this).cascadeOnUpdate(true);
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(Atom.class).cascadeOnDelete(false);
	}

	public void store() {
		CascadeToVectorTestCase ctv = new CascadeToVectorTestCase();
		ctv.vec = new Vector();
		ctv.vec.addElement(new Atom("stored1"));
		ctv.vec.addElement(new Atom(new Atom("storedChild1"), "stored2"));
		store(ctv);
	}

	public void conc(ExtObjectContainer oc) {
		CascadeToVectorTestCase ctv = (CascadeToVectorTestCase) Db4oUtil.getOne(oc,
				CascadeToVectorTestCase.class);
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
		CascadeToVectorTestCase ctv = (CascadeToVectorTestCase) retrieveOnlyInstance(oc,
				CascadeToVectorTestCase.class);
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
		ObjectSet os = oc.query(CascadeToVectorTestCase.class);
		if (os.size() == 0) { // already deleted
			return;
		}
		Assert.areEqual(1, os.size());
		CascadeToVectorTestCase ctv = (CascadeToVectorTestCase) os.next();
		// wait for other threads
		Thread.sleep(500);
		oc.delete(ctv);
	}

	public void checkDelete(ExtObjectContainer oc) {
		// Cascade-On-Delete Test: We only want one atom to remain.
		assertOccurrences(oc, Atom.class, 1);
	}
}
