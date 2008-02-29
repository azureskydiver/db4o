/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.extensions;

import java.lang.reflect.*;

/**
 * Platform dependent code goes here.
 *
 * @sharpen.ignore
 */
public class Db4oUnitPlatform {

	public static boolean isUserField(Field a_field) {
	    return (!Modifier.isStatic(a_field.getModifiers()))
	        && (!Modifier.isTransient(a_field.getModifiers())
	            & !(a_field.getName().indexOf("$") > -1));
	}

	public static boolean isPascalCase() {
		return false;
	}

}
