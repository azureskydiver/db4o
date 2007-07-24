/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.concurrency;

import java.util.*;

import com.db4o.config.*;
import com.db4o.db4ounit.common.persistent.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeToExistingVectorMemberTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new CascadeToExistingVectorMemberTestCase().runConcurrency();
	}
	
	public Vector vec;

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnUpdate(true);
		config.objectClass(Atom.class).cascadeOnUpdate(false);
	}

	protected void store() {
		CascadeToExistingVectorMemberTestCase cev = new CascadeToExistingVectorMemberTestCase();
		cev.vec = new Vector();
		Atom atom = new Atom("one");
		store(atom);
		cev.vec.addElement(atom);
		store(cev);
	}

	public void conc(final ExtObjectContainer oc, final int seq) {
		CascadeToExistingVectorMemberTestCase cev = (CascadeToExistingVectorMemberTestCase) retrieveOnlyInstance(oc, this.getClass());
		Atom atom = (Atom) cev.vec.elementAt(0);
		atom.name = "two" + seq;
		oc.set(cev);
		atom.name = "three" + seq;
		oc.set(cev);
	}

	public void check(final ExtObjectContainer oc) {
		CascadeToExistingVectorMemberTestCase cev = (CascadeToExistingVectorMemberTestCase) retrieveOnlyInstance(oc, CascadeToExistingVectorMemberTestCase.class);
		Atom atom = (Atom) cev.vec.elementAt(0);
		Assert.isTrue(atom.name.startsWith("three"));
		Assert.isTrue(atom.name.length() > "three".length());	
	}
}
