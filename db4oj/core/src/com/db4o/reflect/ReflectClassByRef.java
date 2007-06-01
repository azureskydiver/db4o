/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.reflect;

/**
 * Useful as "out" or "by ref" function parameter.
 */
public final class ReflectClassByRef {
	
	/**
	 * Useful whenever an "out" parameter is to be ignored.
	 */
	public static final ReflectClassByRef IGNORED = new ReflectClassByRef();
	
	public ReflectClass value;
	
	public ReflectClassByRef(ReflectClass initialValue) {
		value = initialValue;
	}
	
	public ReflectClassByRef() {
	}
}
