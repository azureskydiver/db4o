/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.instrumentation.core;

/**
 * Filter for Class instances.
 */
public interface ClassFilter {
	boolean accept(Class clazz);
}
