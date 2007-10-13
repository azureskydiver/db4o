package com.db4o.instrumentation.core;

import EDU.purdue.cs.bloat.editor.*;

public class NullClassEdit implements BloatClassEdit {

	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		return InstrumentationStatus.NOT_INSTRUMENTED;
	}

}
