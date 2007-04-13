/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.*;

import com.db4o.config.*;
import com.db4o.cs.common.util.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class HashtableModifiedUpdateDepth extends AbstractDb4oTestCase {

	Hashtable ht;

	public void configure(Configuration config) {
		config.updateDepth(Integer.MAX_VALUE);
	}

	public void store(ExtObjectContainer oc) {
		ht = new Hashtable();
		ht.put("hi", "five");
		oc.set(this);
	}

	public void test1() {
		ExtObjectContainer oc = db();
		try {
			Hashtable ht1 = (Hashtable) oc.query(Hashtable.class).next();
			ht1.put("hi", "updated");
			Hashtable ht2 = (Hashtable) oc.query(Hashtable.class).next();
			// FIXME: do we have to refresh ? 
			oc.refresh(ht2, Integer.MAX_VALUE);
			Assert.areEqual("five", ht2.get("hi"));
		} finally {
			oc.close();
		}
	}

	public void test2() {
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = db();
		try {
			Hashtable ht1 = (Hashtable) Db4oUtil.getOne(oc1, Hashtable.class);
			Hashtable ht2 = (Hashtable) Db4oUtil.getOne(oc2, Hashtable.class);
			ht1.put("hi", "updated1");
			ht2.put("hi", "updated2");

			// oc1 sets updated value, but doesn't commit
			oc1.set(ht1);
			ht1 = (Hashtable) Db4oUtil.getOne(oc1, Hashtable.class);
			Assert.areEqual("updated1", ht1.get("hi"));
			ht2 = (Hashtable) Db4oUtil.getOne(oc2, Hashtable.class);
			oc2.refresh(ht2, Integer.MAX_VALUE);
			Assert.areEqual("five", ht2.get("hi"));

			// oc1 commits
			oc1.commit();
			ht1 = (Hashtable) Db4oUtil.getOne(oc1, Hashtable.class);
			Assert.areEqual("updated1", ht1.get("hi"));
			ht2 = (Hashtable) Db4oUtil.getOne(oc2, Hashtable.class);
			oc2.refresh(ht2, Integer.MAX_VALUE);
			Assert.areEqual("updated1", ht2.get("hi"));

			// oc2 sets updated value, but doesn't commit
			ht2.put("hi", "updated2");
			oc2.set(ht2);
			ht1 = (Hashtable) Db4oUtil.getOne(oc1, Hashtable.class);
			oc1.refresh(ht1, Integer.MAX_VALUE);
			Assert.areEqual("updated1", ht1.get("hi"));
			ht2 = (Hashtable) Db4oUtil.getOne(oc2, Hashtable.class);
			Assert.areEqual("updated2", ht2.get("hi"));

			// oc2 commits
			oc2.commit();
			ht1 = (Hashtable) Db4oUtil.getOne(oc1, Hashtable.class);
			oc1.refresh(ht1, Integer.MAX_VALUE);
			Assert.areEqual("updated2", ht1.get("hi"));
			ht2 = (Hashtable) Db4oUtil.getOne(oc2, Hashtable.class);
			Assert.areEqual("updated2", ht2.get("hi"));
		} finally {
			oc1.close();
			oc2.close();
		}
	}

	public void conc(ExtObjectContainer oc, int seq) {
		ht = (Hashtable) Db4oUtil.getOne(oc, Hashtable.class);
		ht.put("hi", "updated" + seq);
		oc.set(ht);
	}

	public void check(ExtObjectContainer oc) {
		ht = (Hashtable) Db4oUtil.getOne(oc, Hashtable.class);
		String s = (String) ht.get("hi");
		Assert.isTrue(s.startsWith("updated"));
		Assert.isTrue(s.length() > "updated".length());
	}
}