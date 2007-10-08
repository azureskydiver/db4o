/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.concurrency.collections;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.db4ounit.jre5.collections.*;
import com.db4o.ext.*;
import com.db4o.reflect.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class ArrayList4TestCase extends Db4oConcurrenyTestCase {
	public static void main(String[] args) {
		new ArrayList4TestCase().runEmbeddedConcurrency();
	}

	protected void store() throws Exception {
		ArrayList4<Integer> list = new ArrayList4<Integer>();
		ArrayList4Asserter.createList(list);
		store(list);
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentActivationSupport());
		config.activationDepth(0);
		super.configure(config);
	}
	
	public void conc(ExtObjectContainer oc) throws Exception {
		retrieveAndAssertNullArrayList4(oc);
	}
	
	public void concAdd(ExtObjectContainer oc, int seq) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		markTaskDone(seq, true);
		waitForAllTasksDone();
		ArrayList4Asserter.assertAdd(list);
		oc.set(list);
	}
	
	public void checkAdd(ExtObjectContainer oc) throws Exception {
		ArrayList4<Integer> list = retrieveAndAssertNullArrayList4(oc);
		ArrayList4Asserter.checkAdd(list);
	}

	@SuppressWarnings("unchecked")
	private ArrayList4<Integer> retrieveAndAssertNullArrayList4(ExtObjectContainer oc) throws Exception{
		ArrayList4<Integer> list = (ArrayList4<Integer>) retrieveOnlyInstance(oc, ArrayList4.class);
		assertNullArrayList4(list);
		return list;
	}
	
	private void assertNullArrayList4(ArrayList4<Integer> list) throws Exception {
		Assert.isNull(getField(list, "elements"));
		Assert.areEqual(0, getField(list, "capacity"));
		Assert.areEqual(0, getField(list, "listSize"));
	}
	
	private Object getField(Object parent, String fieldName) {
		ReflectClass parentClazz = reflector().forObject(parent);
		ReflectField field = parentClazz.getDeclaredField(fieldName);
		field.setAccessible();
		return field.get(parent);
	}
}
