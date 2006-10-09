/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.ext.ExtObjectContainer;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class NestedArrays extends ClientServerTestCase {

	public Object _object;

	public Object[] _objectArray;

	private static final int DEPTH = 5;

	private static final int ELEMENTS = 3;

	public NestedArrays() {

	}

	public void store(ExtObjectContainer oc) {
		_object = new Object[ELEMENTS];
		fill((Object[]) _object, DEPTH);

		_objectArray = new Object[ELEMENTS];
		fill(_objectArray, DEPTH);
		oc.set(this);
	}

	private void fill(Object[] arr, int depth) {

		if (depth <= 0) {
			arr[0] = "somestring";
			arr[1] = new Integer(10);
			return;
		}

		depth--;

		for (int i = 0; i < ELEMENTS; i++) {
			arr[i] = new Object[ELEMENTS];
			fill((Object[]) arr[i], depth);
		}
	}

	public void conc(ExtObjectContainer oc) {
		NestedArrays nr = (NestedArrays) Db4oUtil
				.getOne(oc, NestedArrays.class);
		check((Object[]) nr._object, DEPTH);
		check((Object[]) nr._objectArray, DEPTH);
	}

	private void check(Object[] arr, int depth) {
		if (depth <= 0) {
			Assert.areEqual("somestring", arr[0]);
			Assert.areEqual(new Integer(10), arr[1]);
			return;
		}

		depth--;

		for (int i = 0; i < ELEMENTS; i++) {
			check((Object[]) arr[i], depth);
		}

	}

}
