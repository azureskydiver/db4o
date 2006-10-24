/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit;

import java.lang.reflect.*;

/**
 * Platform dependent code goes here.
 *
 * @sharpen.ignore
 */
public class Db4oUnitPlatform {

	public static boolean storeableField(Field a_field) {
		return isStoreableField(a_field);
	}

	public static boolean isStoreableField(Field a_field) {
	    return (!Modifier.isStatic(a_field.getModifiers()))
	        && (!Modifier.isTransient(a_field.getModifiers())
	            & !(a_field.getName().indexOf("$") > -1));
	}

}
