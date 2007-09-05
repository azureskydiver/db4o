/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation;

public class DefaultNativeClassFactory implements NativeClassFactory {

	public Class forName(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

}
