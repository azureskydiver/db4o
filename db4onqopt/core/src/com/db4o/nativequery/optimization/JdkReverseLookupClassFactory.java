/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.optimization;

import com.db4o.instrumentation.core.*;
import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;

public class JdkReverseLookupClassFactory implements NativeClassFactory {

	private Reflector _reflector;
	
	public JdkReverseLookupClassFactory(Reflector reflector) {
		_reflector = reflector;
	}

	public Class forName(String className) throws ClassNotFoundException {
		return JdkReflector.toNative(_reflector.forName(className));
	}

}
