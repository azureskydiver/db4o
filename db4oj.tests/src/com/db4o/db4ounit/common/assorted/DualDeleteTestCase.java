/* Copyright (C) 2007  Versant Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

public class DualDeleteTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new DualDeleteTestCase().runClientServer();
	}

	public Atom atom;

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(this).cascadeOnUpdate(true);
	}

	protected void store() {
		DualDeleteTestCase dd1 = new DualDeleteTestCase();
		dd1.atom = new Atom("justone");
		store(dd1);
		DualDeleteTestCase dd2 = new DualDeleteTestCase();
		dd2.atom = dd1.atom;
		store(dd2);
	}

	public void test() {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		try {
			ObjectSet os1 = oc1.query(DualDeleteTestCase.class);
			ObjectSet os2 = oc2.query(DualDeleteTestCase.class);
			deleteObjectSet(oc1, os1);
			assertOccurrences(oc1, Atom.class, 0);
			assertOccurrences(oc2, Atom.class, 1);
			deleteObjectSet(oc2, os2);
			assertOccurrences(oc1, Atom.class, 0);
			assertOccurrences(oc2, Atom.class, 0);
			oc1.rollback();
			assertOccurrences(oc1, Atom.class, 1);
			assertOccurrences(oc2, Atom.class, 0);
			oc1.commit();
			assertOccurrences(oc1, Atom.class, 1);
			assertOccurrences(oc2, Atom.class, 0);
			deleteAll(oc2, DualDeleteTestCase.class);
			oc2.commit();
			assertOccurrences(oc1, Atom.class, 0);
			assertOccurrences(oc2, Atom.class, 0);
		} finally {
			oc1.close();
			oc2.close();
		}
	}

	public void conc1(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(DualDeleteTestCase.class);
		Thread.sleep(500);
		deleteObjectSet(oc, os);
		oc.rollback();
	}

	public void check1(ExtObjectContainer oc) throws Exception {
		assertOccurrences(oc, Atom.class, 1);
	}

	public void conc2(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(DualDeleteTestCase.class);
		Thread.sleep(500);
		deleteObjectSet(oc, os);
		assertOccurrences(oc, Atom.class, 0);
	}

	public void check2(ExtObjectContainer oc) throws Exception {
		assertOccurrences(oc, Atom.class, 0);
	}

}
