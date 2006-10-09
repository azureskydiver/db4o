/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

/**
 * 
 */
public class IndexedUpdatesWithNull extends ClientServerTestCase {

	public String str;

	public IndexedUpdatesWithNull() {
	}

	public IndexedUpdatesWithNull(String str) {
		this.str = str;
	}

	public void configure(Configuration config) {
		config.objectClass(this).objectField("str").indexed(true);
	}

	public void store(ExtObjectContainer oc) {
		oc.set(new IndexedUpdatesWithNull("one"));
		oc.set(new IndexedUpdatesWithNull("two"));
		oc.set(new IndexedUpdatesWithNull("three"));
		oc.set(new IndexedUpdatesWithNull(null));
		oc.set(new IndexedUpdatesWithNull(null));
		oc.set(new IndexedUpdatesWithNull(null));
		oc.set(new IndexedUpdatesWithNull(null));
		oc.set(new IndexedUpdatesWithNull("four"));
	}

	public void conc1(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(IndexedUpdatesWithNull.class);
		q.descend("str").constrain(null);
		ObjectSet objectSet = q.execute();
		Assert.areEqual(4, objectSet.size());
	}

	public void conc2(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(IndexedUpdatesWithNull.class);
		q.descend("str").constrain(null);
		ObjectSet objectSet = q.execute();
		if (objectSet.size() == 0) { // already set by other threads
			return;
		}
		Assert.areEqual(4, objectSet.size());
		// wait for other threads
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		while (objectSet.hasNext()) {
			IndexedUpdatesWithNull iuwn = (IndexedUpdatesWithNull) objectSet
					.next();
			iuwn.str = "hi";
			oc.set(iuwn);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public void check2(ExtObjectContainer oc) {
		Query q1 = oc.query();
		q1.constrain(IndexedUpdatesWithNull.class);
		q1.descend("str").constrain(null);
		ObjectSet objectSet1 = q1.execute();
		Assert.areEqual(0, objectSet1.size());

		Query q2 = oc.query();
		q2.constrain(IndexedUpdatesWithNull.class);
		q2.descend("str").constrain("hi");
		ObjectSet objectSet2 = q2.execute();
		Assert.areEqual(4, objectSet2.size());
	}

}
