/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.api;

/**
 * @exclude
 */
public interface NativeClassFactory {
	Class forName(String className) throws ClassNotFoundException;
}
