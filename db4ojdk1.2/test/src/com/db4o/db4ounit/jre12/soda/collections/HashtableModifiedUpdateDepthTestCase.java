/* Copyright (C) 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.soda.collections;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class HashtableModifiedUpdateDepthTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new HashtableModifiedUpdateDepthTestCase().runClientServer();
	}
	
	public class Item {
		public Hashtable ht;
	}

	public void configure(Configuration config) {
		config.updateDepth(Integer.MAX_VALUE);
	}

	public void store() {
		Item item = new Item();
		item.ht = new Hashtable();
		item.ht.put("hi", "five");
		store(item);
	}

	public void test() {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		try {
			Hashtable ht1 = (Hashtable) retrieveOnlyInstance(oc1, Hashtable.class);
			Hashtable ht2 = (Hashtable) retrieveOnlyInstance(oc2, Hashtable.class);
			ht1.put("hi", "updated1");
			ht2.put("hi", "updated2");

			// oc1 sets updated value, but doesn't commit
			oc1.set(ht1);
			ht1 = (Hashtable) retrieveOnlyInstance(oc1, Hashtable.class);
			Assert.areEqual("updated1", ht1.get("hi"));
			ht2 = (Hashtable) retrieveOnlyInstance(oc2, Hashtable.class);
			oc2.refresh(ht2, Integer.MAX_VALUE);
			Assert.areEqual("five", ht2.get("hi"));

			// oc1 commits
			oc1.commit();
			ht1 = (Hashtable) retrieveOnlyInstance(oc1, Hashtable.class);
			Assert.areEqual("updated1", ht1.get("hi"));
			ht2 = (Hashtable) retrieveOnlyInstance(oc2, Hashtable.class);
			oc2.refresh(ht2, Integer.MAX_VALUE);
			Assert.areEqual("updated1", ht2.get("hi"));

			// oc2 sets updated value, but doesn't commit
			ht2.put("hi", "updated2");
			oc2.set(ht2);
			ht1 = (Hashtable) retrieveOnlyInstance(oc1, Hashtable.class);
			oc1.refresh(ht1, Integer.MAX_VALUE);
			Assert.areEqual("updated1", ht1.get("hi"));
			ht2 = (Hashtable) retrieveOnlyInstance(oc2, Hashtable.class);
			Assert.areEqual("updated2", ht2.get("hi"));

			// oc2 commits
			oc2.commit();
			ht1 = (Hashtable) retrieveOnlyInstance(oc1, Hashtable.class);
			oc1.refresh(ht1, Integer.MAX_VALUE);
			Assert.areEqual("updated2", ht1.get("hi"));
			ht2 = (Hashtable) retrieveOnlyInstance(oc2, Hashtable.class);
			Assert.areEqual("updated2", ht2.get("hi"));
		} finally {
			oc1.close();
			oc2.close();
		}
	}

}