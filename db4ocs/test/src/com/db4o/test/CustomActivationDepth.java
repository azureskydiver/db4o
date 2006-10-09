/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

/**
 * 
 */
public class CustomActivationDepth extends ClientServerTestCase {

	int myInt;

	String myString;

	int[] ints;

	String[] strings;

	CA1 ca1;

	CA2 ca2;

	CA3 ca3;

	CA1[] ca1s;

	CA2[] ca2s;

	CA3[] ca3s;

	public void store(ExtObjectContainer oc) {
		myInt = 7;
		myString = "seven";
		ints = new int[] { 77 };
		strings = new String[] { "sevenseven" };
		ca1 = new CA1("1");
		ca2 = new CA2("2");
		ca3 = new CA3("3");

		ca1s = new CA1[] { new CA1("1arr1"), new CA1("1arr2") };
		ca2s = new CA2[] { new CA2("2arr1"), new CA2("2arr2") };
		ca3s = new CA3[] { new CA3("3arr1"), new CA3("3arr2") };

		oc.set(this);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		oc.configure().objectClass(CustomActivationDepth.class)
				.maximumActivationDepth(seq);
		oc.configure().objectClass(CA1.class).maximumActivationDepth(1);
		CustomActivationDepth cad = (CustomActivationDepth) Db4oUtil.getOne(oc,
				CustomActivationDepth.class);
		oc.activate(cad, seq);
		oc.activate(cad.ca1, 10);
		Assert.isNull(cad.ca1.ca2.name);
		Assert.areEqual("1", cad.ca1.name);
		if (seq <= 1) {
			// FIXME: the assertion fails sometimes (randomly). "Expected
			// reference to be null, but was 2".
			Assert.isNull(cad.ca2.name);
			Assert.isNull(cad.ca3.name);
			Assert.isNull(cad.ca1s[0].name);
			Assert.isNull(cad.ca1s[1].name);
			Assert.isNull(cad.ca2s[0].name);
			Assert.isNull(cad.ca2s[1].name);
			Assert.isNull(cad.ca3s[0].name);
			Assert.isNull(cad.ca3s[1].name);
		} else {
			Assert.areEqual("2", cad.ca2.name);
			Assert.areEqual("3", cad.ca3.name);
			Assert.areEqual("1arr1", cad.ca1s[0].name);
			Assert.areEqual("1arr2", cad.ca1s[1].name);
			Assert.areEqual("2arr1", cad.ca2s[0].name);
			Assert.areEqual("2arr2", cad.ca2s[1].name);
			Assert.areEqual("3arr1", cad.ca3s[0].name);
			Assert.areEqual("3arr2", cad.ca3s[1].name);
		}
	}

	public static class CA1 {

		public String name;

		public CA2 ca2;

		public CA1() {

		}

		public CA1(String name) {
			this.name = name;
			ca2 = new CA2(name + ".2");
		}

	}

	public static class CA2 {

		public String name;

		public CA3 ca3;

		public CA2() {

		}

		public CA2(String name) {
			this.name = name;
			ca3 = new CA3(name + ".3");
		}

	}

	public static class CA3 {

		public String name;

		public CA3() {

		}

		public CA3(String name) {
			this.name = name;
		}

	}

}
