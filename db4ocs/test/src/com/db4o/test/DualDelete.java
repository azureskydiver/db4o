/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.persistent.Atom;

import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class DualDelete extends ClientServerTestCase {

	public Atom atom;

	public void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(this).cascadeOnUpdate(true);
	}

	public void store(ExtObjectContainer oc) {
		DualDelete dd1 = new DualDelete();
		dd1.atom = new Atom("justone");
		oc.set(dd1);
		DualDelete dd2 = new DualDelete();
		dd2.atom = dd1.atom;
		oc.set(dd2);
	}

	public void test() {
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = db();
		try {
			ObjectSet os1 = oc1.query(DualDelete.class);
			ObjectSet os2 = oc2.query(DualDelete.class);
			Db4oUtil.deleteObjectSet(oc1, os1);
			Db4oUtil.assertOccurrences(oc1, Atom.class, 0);
			Db4oUtil.assertOccurrences(oc2, Atom.class, 1);
			Db4oUtil.deleteObjectSet(oc2, os2);
			Db4oUtil.assertOccurrences(oc1, Atom.class, 0);
			Db4oUtil.assertOccurrences(oc2, Atom.class, 0);
			oc1.rollback();
			Db4oUtil.assertOccurrences(oc1, Atom.class, 1);
			Db4oUtil.assertOccurrences(oc2, Atom.class, 0);
			oc1.commit();
			Db4oUtil.assertOccurrences(oc1, Atom.class, 1);
			// FIXME: the following assertion fails
			Db4oUtil.assertOccurrences(oc2, Atom.class, 1);
			Db4oUtil.deleteAll(oc2, DualDelete.class);
			oc2.commit();
			Db4oUtil.assertOccurrences(oc1, Atom.class, 0);
			Db4oUtil.assertOccurrences(oc2, Atom.class, 0);
		} finally {
			oc1.close();
			oc2.close();
		}
	}

	public void conc1(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(DualDelete.class);
		Thread.sleep(500);
		Db4oUtil.deleteObjectSet(oc, os);
		oc.rollback();
	}

	public void check1(ExtObjectContainer oc) throws Exception {
		Db4oUtil.assertOccurrences(oc, Atom.class, 1);
	}

	public void conc2(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(DualDelete.class);
		Thread.sleep(500);
		Db4oUtil.deleteObjectSet(oc, os);
		// FIXME: the following assertion fails
		Db4oUtil.assertOccurrences(oc, Atom.class, 0);
	}

	public void check2(ExtObjectContainer oc) throws Exception {
		Db4oUtil.assertOccurrences(oc, Atom.class, 0);
	}

}
