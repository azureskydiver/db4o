/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;
import com.db4o.test.persistent.Atom;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

/**
 * 
 */
public class IndexedByIdentity extends ClientServerTestCase {

	public Atom atom;

	static final int COUNT = 10;

	public void configure(Configuration config) {
		config.objectClass(this).objectField("atom").indexed(true);
	}

	public void store(ExtObjectContainer oc) {
		for (int i = 0; i < COUNT; i++) {
			IndexedByIdentity ibi = new IndexedByIdentity();
			ibi.atom = new Atom("ibi" + i);
			oc.set(ibi);
		}
	}

	public void concRead(ExtObjectContainer oc) {
		for (int i = 0; i < COUNT; i++) {
			Query q = oc.query();
			q.constrain(Atom.class);
			q.descend("name").constrain("ibi" + i);
			ObjectSet objectSet = q.execute();
			Assert.areEqual(1, objectSet.size());
			Atom child = (Atom) objectSet.next();
			q = oc.query();
			q.constrain(IndexedByIdentity.class);
			q.descend("atom").constrain(child).identity();
			objectSet = q.execute();
			Assert.areEqual(1, objectSet.size());
			IndexedByIdentity ibi = (IndexedByIdentity) objectSet.next();
			Assert.areSame(child, ibi.atom);
		}

	}

	public void concUpdate(ExtObjectContainer oc, int seq) {
		oc.configure().objectClass(IndexedByIdentity.class).cascadeOnUpdate(true);
		Query q = oc.query();
		q.constrain(IndexedByIdentity.class);
		ObjectSet os = q.execute();
		Assert.areEqual(COUNT, os.size());
		while (os.hasNext()) {
			IndexedByIdentity idi = (IndexedByIdentity) os.next();
			idi.atom.name = "updated" + seq;
			oc.set(idi);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
	}

	public void checkUpdate(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(IndexedByIdentity.class);
		ObjectSet os = q.execute();
		Assert.areEqual(COUNT, os.size());
		String expected = null;
		while (os.hasNext()) {
			IndexedByIdentity idi = (IndexedByIdentity) os.next();
			if (expected == null) {
				expected = idi.atom.name;
				Assert.isTrue(expected.startsWith("updated"));
				Assert.isTrue(expected.length() > "updated".length());
			}
			Assert.areEqual(expected, idi.atom.name);
		}
	}

}
