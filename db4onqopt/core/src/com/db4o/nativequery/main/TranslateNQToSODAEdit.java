/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.main;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.instrumentation.*;
import com.db4o.nativequery.optimization.*;
import com.db4o.query.*;

/**
 * @exclude
 */
public class TranslateNQToSODAEdit implements BloatClassEdit {

	private final NativeQueryEnhancer _enhancer;
	private final BloatUtil _bloatUtil;
	
	public TranslateNQToSODAEdit(NativeQueryEnhancer enhancer, BloatUtil bloatUtil) {
		_enhancer = enhancer;
		_bloatUtil = bloatUtil;
	}
	
	public boolean bloat(ClassEditor ce, ClassLoader origLoader) {
		try {
			Type type=ce.superclass();
			while(type!=null) {
				if(BloatUtil.normalizeClassName(type.className()).equals(Predicate.class.getName())) {
					_enhancer.enhance(_bloatUtil,ce,Predicate.PREDICATEMETHOD_NAME,null,origLoader, new DefaultClassSource());
					return true;
				}
				type = _bloatUtil.superType(type);
			}
			//System.err.println("Bypassing "+ce.name());
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		}
		return false;
	}

}
