/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.main;

import java.net.*;

import EDU.purdue.cs.bloat.file.*;

import com.db4o.instrumentation.*;
import com.db4o.nativequery.optimization.*;
import com.db4o.query.*;

public class Db4oEnhancingClassloader extends BloatInstrumentingClassLoader {
	
	public Db4oEnhancingClassloader(ClassLoader delegate) {
		super(new URL[]{},delegate, new AssignableClassFilter(Predicate.class), new TranslateNQToSODAEdit());
	}
}
