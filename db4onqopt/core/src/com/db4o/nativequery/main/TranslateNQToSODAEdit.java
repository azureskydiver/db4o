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

	private final NativeQueryEnhancer _enhancer = new NativeQueryEnhancer();
	
	public boolean bloat(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		try {
			Type type=ce.superclass();
			while(type!=null) {
				if(BloatUtil.normalizeClassName(type.className()).equals(Predicate.class.getName())) {
					return _enhancer.enhance(loaderContext,ce,Predicate.PREDICATEMETHOD_NAME,null,origLoader, new DefaultClassSource());
				}
				type = loaderContext.superType(type);
			}
			//System.err.println("Bypassing "+ce.name());
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		}
		return false;
	}

}
