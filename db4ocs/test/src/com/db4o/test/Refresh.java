/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class Refresh extends ClientServerTestCase {

	public String name;

	public Refresh child;

	public Refresh() {

	}

	public Refresh(String name, Refresh child) {
		this.name = name;
		this.child = child;
	}

	public void store(ExtObjectContainer oc) {
		Refresh r3 = new Refresh("o3", null);
		Refresh r2 = new Refresh("o2", r3);
		Refresh r1 = new Refresh("o1", r2);
		oc.set(r1);
	}

	public void conc(ExtObjectContainer oc) {
		Refresh r11 = getRoot(oc);
		r11.name = "cc";
		oc.refresh(r11, 0);
		Assert.areEqual("cc", r11.name);
		oc.refresh(r11, 1);
		Assert.areEqual("o1", r11.name);
		r11.child.name = "cc";
		oc.refresh(r11, 1);
		Assert.areEqual("cc", r11.child.name);
		oc.refresh(r11, 2);
		Assert.areEqual("o2", r11.child.name);
	}

	public void test() {
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = db();
		try {
			Refresh r1 = getRoot(oc1);
			r1.name = "cc";
			oc1.refresh(r1, 0);
			Assert.areEqual("cc", r1.name);
			oc1.refresh(r1, 1);
			Assert.areEqual("o1", r1.name);
			r1.child.name = "cc";
			oc1.refresh(r1, 1);
			Assert.areEqual("cc", r1.child.name);
			oc1.refresh(r1, 2);
			Assert.areEqual("o2", r1.child.name);

			oc2.configure().objectClass(Refresh.class).cascadeOnUpdate(true);
			Refresh r2 = getRoot(oc2);
			r2.name = "o21";
			r2.child.name = "o22";
			r2.child.child.name = "o23";
			oc2.set(r2);
			oc2.commit();

			oc1.refresh(r1, 3);
			Assert.areEqual("o21", r1.name);
			Assert.areEqual("o22", r1.child.name);
			Assert.areEqual("o23", r1.child.child.name);
		} finally {
			oc1.close();
			oc2.close();
		}
	}

	private Refresh getRoot(ObjectContainer oc) {
		Query q = oc.query();
		q.constrain(Refresh.class);
		q.descend("name").constrain("o1");
		ObjectSet objectSet = q.execute();
		return (Refresh) objectSet.next();
	}

}
