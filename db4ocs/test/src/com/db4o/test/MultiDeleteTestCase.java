/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class MultiDeleteTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new MultiDeleteTestCase().runConcurrency();
	}
	
	MultiDeleteTestCase child;

	String name;

	Object forLong;

	Long myLong;

	Object[] untypedArr;

	Long[] typedArr;

	public void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(this).cascadeOnUpdate(true);
	}

	public void store() {
		MultiDeleteTestCase md = new MultiDeleteTestCase();
		md.name = "killmefirst";
		md.setMembers();
		md.child = new MultiDeleteTestCase();
		md.child.setMembers();
		store(md);
	}

	public void conc(ExtObjectContainer oc) throws Exception {
		Query q = oc.query();
		q.constrain(MultiDeleteTestCase.class);
		q.descend("name").constrain("killmefirst");
		ObjectSet objectSet = q.execute();
		if (objectSet.size() == 0) { // already deleted by other threads
			return;
		}
		
		Assert.areEqual(1, objectSet.size());
		Thread.sleep(1000);
		MultiDeleteTestCase md = (MultiDeleteTestCase) objectSet.next();
		oc.delete(md);
		assertCountOccurences(oc, MultiDeleteTestCase.class, 0);
	}

	public void check(ExtObjectContainer oc) {
		assertCountOccurences(oc, MultiDeleteTestCase.class, 0);
	}

	private void setMembers() {
		forLong = new Long(100);
		myLong = new Long(100);
		untypedArr = new Object[] { new Long(10), "hi", new MultiDeleteTestCase() };
		typedArr = new Long[] { new Long(3), new Long(7), new Long(9), };
	}

}
