/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

/**
 * 
 */
public class DeepSet extends ClientServerTestCase {

	public DeepSet child;

	public String name;

	public void store(ExtObjectContainer oc) {
		name = "1";
		child = new DeepSet();
		child.name = "2";
		child.child = new DeepSet();
		child.child.name = "3";
		oc.set(this, 3);
	}

	public void test() {
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = db();
		ExtObjectContainer oc3 = db();
		try {
			DeepSet example = new DeepSet();
			example.name = "1";
			DeepSet ds1 = (DeepSet) oc1.get(example).next();
			Assert.areEqual("1", ds1.name);
			Assert.areEqual("2", ds1.child.name);
			Assert.areEqual("3", ds1.child.child.name);

			DeepSet ds2 = (DeepSet) oc1.get(example).next();
			Assert.areEqual("1", ds2.name);
			Assert.areEqual("2", ds2.child.name);
			Assert.areEqual("3", ds2.child.child.name);

			ds1.child.name = "12";
			ds1.child.child.name = "13";
			oc1.set(ds1, 2);
			oc1.commit();

			// check result
			DeepSet ds = (DeepSet) oc1.get(example).next();
			Assert.areEqual("1", ds.name);
			Assert.areEqual("12", ds.child.name);
			Assert.areEqual("13", ds.child.child.name);

			ds = (DeepSet) oc2.get(example).next();
			Assert.areEqual("1", ds.name);
			Assert.areEqual("12", ds.child.name);
			Assert.areEqual("3", ds.child.child.name);

			ds = (DeepSet) oc3.get(example).next();
			Assert.areEqual("1", ds.name);
			Assert.areEqual("12", ds.child.name);
			Assert.areEqual("3", ds.child.child.name);
		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
		}
	}

	public void conc(ExtObjectContainer oc, int seq) {
		DeepSet example = new DeepSet();
		example.name = "1";
		DeepSet ds = (DeepSet) oc.get(example).next();
		Assert.areEqual("1", ds.name);
		Assert.areEqual("3", ds.child.child.name);
		ds.name = "1";
		ds.child.name = "12" + seq;
		ds.child.child.name = "13" + seq;
		oc.set(ds, 2);
	}

	public void check(ExtObjectContainer oc) {
		DeepSet example = new DeepSet();
		example.name = "1";
		DeepSet ds = (DeepSet) oc.get(example).next();
		Assert.isTrue(ds.child.name.startsWith("12"));
		Assert.isTrue(ds.child.name.length() > "12".length());
		Assert.areEqual("3", ds.child.child.name);
	}

}
