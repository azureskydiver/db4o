/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class PersistStaticFieldValuesTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new PersistStaticFieldValuesTestCase().runConcurrency();
	}

	public static final PsfvHelper ONE = new PsfvHelper();

	public static final PsfvHelper TWO = new PsfvHelper();

	public static final PsfvHelper THREE = new PsfvHelper();

	public PsfvHelper one;

	public PsfvHelper two;

	public PsfvHelper three;

	public void configure(Configuration config) {
		config.objectClass(PersistStaticFieldValuesTestCase.class)
				.persistStaticFieldValues();
	}

	public void store() {
		PersistStaticFieldValuesTestCase psfv = new PersistStaticFieldValuesTestCase();
		psfv.one = ONE;
		psfv.two = TWO;
		psfv.three = THREE;
		store(psfv);
	}

	public void conc(ExtObjectContainer oc) {
		PersistStaticFieldValuesTestCase psfv = (PersistStaticFieldValuesTestCase) retrieveOnlyInstance(
				oc, PersistStaticFieldValuesTestCase.class);
		Assert.areSame(ONE, psfv.one);
		Assert.areSame(TWO, psfv.two);
		Assert.areSame(THREE, psfv.three);
	}

	public static class PsfvHelper {

	}

}
