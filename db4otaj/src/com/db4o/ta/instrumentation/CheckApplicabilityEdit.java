/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.ta.instrumentation;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.util.*;

/**
 * @exclude
 */
class CheckApplicabilityEdit implements BloatClassEdit {

	private ClassFilter _filter;

	public CheckApplicabilityEdit(ClassFilter filter) {
		_filter = filter;
	}

	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		try {
			Class clazz = BloatUtil.classForEditor(ce, origLoader);
			if (clazz.isInterface()) {
				return InstrumentationStatus.FAILED;
			}
			if (!isApplicableClass(clazz)) {
				return InstrumentationStatus.FAILED;
			}
		} catch (ClassNotFoundException e) {
			return InstrumentationStatus.FAILED;
		}
		return InstrumentationStatus.NOT_INSTRUMENTED;
	}

	private boolean isApplicableClass(Class clazz) {
		Class curClazz = clazz;
		// FIXME string reference to Enum is rather fragile
		while (curClazz != Object.class && curClazz != null && !curClazz.getName().equals("java.lang.Enum")) {
			if (BloatUtil.isPlatformClassName(curClazz.getName())) {
				return false;
			}
			curClazz = curClazz.getSuperclass();
		}
		return true;
	}

	
}
