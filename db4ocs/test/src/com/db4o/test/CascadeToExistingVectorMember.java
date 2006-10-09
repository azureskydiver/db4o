/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.Vector;

import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.test.persistent.Atom;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class CascadeToExistingVectorMember extends ClientServerTestCase {

	public Vector vec;

	public void configure(Configuration config) {
		config.objectClass(this).cascadeOnUpdate(true);
		config.objectClass(Atom.class).cascadeOnUpdate(false);
	}

	public void store(ExtObjectContainer oc) {
		CascadeToExistingVectorMember cev = new CascadeToExistingVectorMember();
		cev.vec = new Vector();
		Atom atom = new Atom("one");
		oc.set(atom);
		cev.vec.addElement(atom);
		oc.set(cev);
	}

	public void conc(final ExtObjectContainer oc, final int seq) {
		CascadeToExistingVectorMember cev = (CascadeToExistingVectorMember) Db4oUtil
				.getOne(oc, this);
		Atom atom = (Atom) cev.vec.elementAt(0);
		atom.name = "two" + seq;
		oc.set(cev);
		atom.name = "three" + seq;
		oc.set(cev);
	}

	public void check(final ExtObjectContainer oc) {
		CascadeToExistingVectorMember cev = (CascadeToExistingVectorMember) Db4oUtil
				.getOne(oc, this);
		Atom atom = (Atom) cev.vec.elementAt(0);
		Assert.isTrue(atom.name.startsWith("three"));
		Assert.isTrue(atom.name.length() > "three".length());	
	}
}
