/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.instrumentation.core;

import EDU.purdue.cs.bloat.editor.*;

public interface BloatClassEdit {
	InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext);
}
