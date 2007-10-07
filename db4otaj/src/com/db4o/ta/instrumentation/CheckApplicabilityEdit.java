/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.ta.instrumentation;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.instrumentation.*;

/**
 * @exclude
 */
public class CheckApplicabilityEdit implements BloatClassEdit {

	private ClassFilter _filter;

	public CheckApplicabilityEdit(ClassFilter filter) {
		_filter = filter;
	}

	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		try {
			Class clazz = BloatUtil.classForEditor(ce, origLoader);
			if (!_filter.accept(clazz)) {
				return InstrumentationStatus.NOT_INSTRUMENTED;
			}
			if (!isApplicableClass(clazz)) {
				throw new IllegalArgumentException(clazz.getName());
			}
		} catch (ClassNotFoundException e) {
			return InstrumentationStatus.FAILED;
		}
		return InstrumentationStatus.NOT_INSTRUMENTED;
	}

	private boolean isApplicableClass(Class clazz) {
		Class curClazz = clazz;
		while (curClazz != Object.class) {
			if (BloatUtil.isPlatformClassName(curClazz.getName())) {
				return false;
			}
			curClazz = curClazz.getSuperclass();
		}
		return true;
	}

	
}
