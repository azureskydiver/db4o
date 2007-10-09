/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.*;
import com.db4o.ext.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class CollectionsUtil {
	@SuppressWarnings("unchecked")
	public static ArrayList4<Integer> retrieveAndAssertNullArrayList4(
			ExtObjectContainer oc, Reflector reflector) throws Exception {
		ArrayList4<Integer> list = (ArrayList4<Integer>) AbstractDb4oTestCase
				.retrieveOnlyInstance(oc, ArrayList4.class);
		assertNullArrayList4(list, reflector);
		return list;
	}

	private static void assertNullArrayList4(ArrayList4<Integer> list,
			Reflector reflector) throws Exception {
		Assert.isNull(getField(reflector, list, "elements"));
		Assert.areEqual(0, getField(reflector, list, "capacity"));
		Assert.areEqual(0, getField(reflector, list, "listSize"));
	}

	private static Object getField(Reflector reflector, Object parent,
			String fieldName) {
		ReflectClass parentClazz = reflector.forObject(parent);
		ReflectField field = parentClazz.getDeclaredField(fieldName);
		field.setAccessible();
		return field.get(parent);
	}
}
