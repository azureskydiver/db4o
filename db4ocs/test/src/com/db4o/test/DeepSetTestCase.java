/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeepSetTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new DeepSetTestCase().runConcurrency();
	}
	
	public DeepSetTestCase child;

	public String name;

	public void store() {
		name = "1";
		child = new DeepSetTestCase();
		child.name = "2";
		child.child = new DeepSetTestCase();
		child.child.name = "3";
		store(this);
	}

	public void test() {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		ExtObjectContainer oc3 = openNewClient();
		try {
			DeepSetTestCase example = new DeepSetTestCase();
			example.name = "1";
			DeepSetTestCase ds1 = (DeepSetTestCase) oc1.get(example).next();
			Assert.areEqual("1", ds1.name);
			Assert.areEqual("2", ds1.child.name);
			Assert.areEqual("3", ds1.child.child.name);

			DeepSetTestCase ds2 = (DeepSetTestCase) oc1.get(example).next();
			Assert.areEqual("1", ds2.name);
			Assert.areEqual("2", ds2.child.name);
			Assert.areEqual("3", ds2.child.child.name);

			ds1.child.name = "12";
			ds1.child.child.name = "13";
			oc1.set(ds1, 2);
			oc1.commit();

			// check result
			DeepSetTestCase ds = (DeepSetTestCase) oc1.get(example).next();
			Assert.areEqual("1", ds.name);
			Assert.areEqual("12", ds.child.name);
			Assert.areEqual("13", ds.child.child.name);

			ds = (DeepSetTestCase) oc2.get(example).next();
			Assert.areEqual("1", ds.name);
			Assert.areEqual("12", ds.child.name);
			Assert.areEqual("3", ds.child.child.name);

			ds = (DeepSetTestCase) oc3.get(example).next();
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
		DeepSetTestCase example = new DeepSetTestCase();
		example.name = "1";
		DeepSetTestCase ds = (DeepSetTestCase) oc.get(example).next();
		Assert.areEqual("1", ds.name);
		Assert.areEqual("3", ds.child.child.name);
		ds.name = "1";
		ds.child.name = "12" + seq;
		ds.child.child.name = "13" + seq;
		oc.set(ds, 2);
	}

	public void check(ExtObjectContainer oc) {
		DeepSetTestCase example = new DeepSetTestCase();
		example.name = "1";
		DeepSetTestCase ds = (DeepSetTestCase) oc.get(example).next();
		Assert.isTrue(ds.child.name.startsWith("12"));
		Assert.isTrue(ds.child.name.length() > "12".length());
		Assert.areEqual("3", ds.child.child.name);
	}

}
