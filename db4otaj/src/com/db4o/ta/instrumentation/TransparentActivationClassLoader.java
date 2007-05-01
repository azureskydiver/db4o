package com.db4o.ta.instrumentation;

import java.io.*;
import java.net.*;
import java.util.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;

import com.db4o.ta.*;

/*
 * TODO: COR-591 - Lots of copy & paste from db4onqopt/Db4oEnhancingClassLoader
 */

public class TransparentActivationClassLoader extends BloatingClassLoader {

	private Map _cache = new HashMap();
	private ClassFilter _filter;

	public TransparentActivationClassLoader(URL[] urls, ClassLoader parent, ClassFilter filter) {
		super(urls, parent);
		_filter = filter;
	}

	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(_cache.containsKey(name)) {
			return (Class)_cache.get(name);
		}
		Class delegateClass = super.loadClass(name,resolve);
		
		if(mustDelegate(name)) {
			return delegateClass;
		}
		Class clazz=(_filter.accept(delegateClass) ? findClass(name) : findRawClass(name));
		_cache.put(clazz.getName(), clazz);
		if(resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}

	private boolean mustDelegate(String name) {
		return name.startsWith("java.")
				|| name.startsWith("javax.")
				||name.startsWith("sun.")
				||((name.startsWith("com.db4o.") && name.indexOf("test.")<0));
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
		ce.addInterface(Activatable.class);
	}

}
