/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.cs.common.util.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ArrayNOrder extends AbstractDb4oTestCase {

	public String[][][] s1;

	public Object[][] o1;

	/**
	 * Stores data to <code>oc</code>, which is generated by test framework
	 * via invoking Db4oTestCase.db(). The <code>oc</code> will be closed by
	 * test framework after executing method, therefore, no need to close
	 * <code>oc</code> in this method.
	 * 
	 * @param oc
	 *            ExtObjectContainer generated by test framework
	 * 
	 */
	public void store(ExtObjectContainer oc) {
		s1 = new String[2][2][3];
		s1[0][0][0] = "000";
		s1[0][0][1] = "001";
		s1[0][0][2] = "002";
		s1[0][1][0] = "010";
		s1[0][1][1] = "011";
		s1[0][1][2] = "012";
		s1[1][0][0] = "100";
		s1[1][0][1] = "101";
		s1[1][0][2] = "102";
		s1[1][1][0] = "110";
		s1[1][1][1] = "111";
		s1[1][1][2] = "112";

		o1 = new Object[2][2];
		o1[0][0] = new Integer(0);
		o1[0][1] = "01";
		o1[1][0] = new Float(10);
		o1[1][1] = new Double(1.1);
		oc.set(this);
	}

	/**
	 * Test db4o with <code>oc</code>, which is generated by test framework
	 * via invoking Db4oTestCase.db(). The <code>oc</code> will be closed by
	 * test framework after executing method, therefore, no need to close
	 * <code>oc</code> in this method.
	 * 
	 * @param oc
	 *            ExtObjectContainer generated by test framework
	 * 
	 */
	public void conc(ExtObjectContainer oc) {
		ArrayNOrder ano = (ArrayNOrder) Db4oUtil.getOne(oc, this);
		ano.check();
	}

	public void concIndexed1(ExtObjectContainer oc) {
		oc.configure().objectClass(ArrayNOrder.class).objectField("o1").indexed(true);
		oc.configure().objectClass(ArrayNOrder.class).objectField("s1").indexed(true);
		conc(oc);
	}
	public void concIndexed2(ExtObjectContainer oc) {
		oc.configure().objectClass(ArrayNOrder.class).objectField("o1").indexed(true);
		oc.configure().objectClass(ArrayNOrder.class).objectField("s1").indexed(false);
		conc(oc);
	}
	
	public void concIndexed3(ExtObjectContainer oc) {
		oc.configure().objectClass(ArrayNOrder.class).objectField("o1").indexed(false);
		oc.configure().objectClass(ArrayNOrder.class).objectField("s1").indexed(true);
		conc(oc);
	}
	
	public void check() {
		Assert.areEqual(s1[0][0][0], "000");
		Assert.areEqual(s1[0][0][1], "001");
		Assert.areEqual(s1[0][0][2], "002");
		Assert.areEqual(s1[0][1][0], "010");
		Assert.areEqual(s1[0][1][1], "011");
		Assert.areEqual(s1[0][1][2], "012");
		Assert.areEqual(s1[1][0][0], "100");
		Assert.areEqual(s1[1][0][1], "101");
		Assert.areEqual(s1[1][0][2], "102");
		Assert.areEqual(s1[1][1][0], "110");
		Assert.areEqual(s1[1][1][1], "111");
		Assert.areEqual(s1[1][1][2], "112");
		Assert.areEqual(o1[0][0], new Integer(0));
		Assert.areEqual(o1[0][1], "01");
		Assert.areEqual(o1[1][0], new Float(10));
		Assert.areEqual(o1[1][1], new Double(1.1));
	}
}
