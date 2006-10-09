/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class MultiDelete extends ClientServerTestCase {

	MultiDelete child;

	String name;

	Object forLong;

	Long myLong;

	Object[] untypedArr;

	Long[] typedArr;

	public void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(this).cascadeOnUpdate(true);
	}

	public void store(ExtObjectContainer oc) {
		MultiDelete md = new MultiDelete();
		md.name = "killmefirst";
		md.setMembers();
		md.child = new MultiDelete();
		md.child.setMembers();
		oc.set(md);
	}

	public void conc(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(MultiDelete.class);
		q.descend("name").constrain("killmefirst");
		ObjectSet objectSet = q.execute();
		if (objectSet.size() == 0) { // already deleted by other threads
			return;
		}
		
		Assert.areEqual(1, objectSet.size());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
		MultiDelete md = (MultiDelete) objectSet.next();
		oc.delete(md);
		Db4oUtil.assertOccurrences(oc, MultiDelete.class, 0);
	}

	public void check(ExtObjectContainer oc) {
		Db4oUtil.assertOccurrences(oc, MultiDelete.class, 0);
	}

	private void setMembers() {
		forLong = new Long(100);
		myLong = new Long(100);
		untypedArr = new Object[] { new Long(10), "hi", new MultiDelete() };
		typedArr = new Long[] { new Long(3), new Long(7), new Long(9), };
	}

}
