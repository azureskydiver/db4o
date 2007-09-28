/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.instrumentation;

import java.io.*;
import java.net.*;
import java.util.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;

/*
 * TODO: COR-591 - Lots of copy & paste from db4onqopt/Db4oEnhancingClassLoader, BloatUtil, SODABloatMethodBuilder, etc.
 */

public class BloatInstrumentingClassLoader extends BloatingClassLoader {

	private final Map _cache = new HashMap();
	private final ClassFilter _filter;
	private final BloatClassEdit _edit;
	private final BloatLoaderContext _loaderContext = new BloatLoaderContext(getClassInfoLoader(), getEditorContext());

	public BloatInstrumentingClassLoader(URL[] urls, ClassLoader parent, ClassFilter filter, BloatClassEdit edit) {
		super(urls, parent);
		_filter = filter;
		_edit = edit;
	}

	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(_cache.containsKey(name)) {
			return (Class)_cache.get(name);
		}
		Class originalClazz = super.loadClass(name, resolve);
		if(mustDelegate(name)) {
			return originalClazz;
		}
		Class clazz=(_filter.accept(originalClazz) ? findClass(name) : findRawClass(name));
		_cache.put(clazz.getName(), clazz);
		if(resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}

	private boolean mustDelegate(String name) {
		return BloatUtil.isPlatformClassName(name)
				||((name.startsWith("com.db4o.") && name.indexOf("test.")<0 && name.indexOf("samples.")<0));
	}
	
	private Class findRawClass(String className) throws ClassNotFoundException {
        try {
			String resourcePath = className.replace('.','/') + ".class";
			InputStream resourceStream = getResourceAsStream(resourcePath);
			ByteArrayOutputStream rawByteStream = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int bytesread = 0;
			while((bytesread = resourceStream.read(buf)) >= 0) {
				rawByteStream.write(buf, 0, bytesread);
			}
			resourceStream.close();
			byte[] rawBytes = rawByteStream.toByteArray();
			return super.defineClass(className, rawBytes, 0, rawBytes.length);
		} catch (Exception exc) {
			throw new ClassNotFoundException(className, exc);
		}	
	}

	protected void bloat(ClassEditor ce) {
		_edit.bloat(ce, getParent(), _loaderContext);
	}

}
