/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.instrumentation.bloat;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;

import com.db4o.instrumentation.api.*;
import com.db4o.instrumentation.util.*;


public class BloatTypeEditor implements TypeEditor, TypeLoader {

	private final ClassEditor _classEditor;
	private final ClassLoader _classLoader;
	private final ClassSource _classSource;
	private final BloatReferenceProvider _references;

	public BloatTypeEditor(ClassEditor classEditor, ClassLoader classLoader, ClassSource classSource) {
		_classEditor = classEditor;
		_classLoader = classLoader;
		_classSource = classSource;
		_references = new BloatReferenceProvider();
	}

	public Class actualType() throws InstrumentationException {
		try {
			return _classLoader.loadClass(BloatUtil.normalizeClassName(_classEditor.name()));
		} catch (ClassNotFoundException e) {
			throw new InstrumentationException(e);
		}
	}

	public void addInterface(Class type) {
		_classEditor.addInterface(type);
	}

	public MethodBuilder newPublicMethod(String methodName, Class returnType, Class[] parameterTypes) {
		return new BloatMethodBuilder(_references, _classEditor, methodName, returnType, parameterTypes);
	}

	public ReferenceProvider references() {
		return _references;
	}

	public TypeLoader loader() {
		return this;
	}

	public Class loadType(String typeName) {
		try {
			return _classSource.loadClass(typeName);
		} catch (ClassNotFoundException e) {
			throw new InstrumentationException(e);
		}
	}

}
