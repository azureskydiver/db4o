/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.nativequery.main;

import java.io.*;
import java.net.*;
import java.util.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.nativequery.bloat.*;
import com.db4o.nativequery.optimization.*;
import com.db4o.query.*;

// TODO: implement 'good guy'/'bad guy' mode switch for delegation?
public class Db4oEnhancingClassloader extends BloatingClassLoader {
	private NativeQueryEnhancer enhancer=new NativeQueryEnhancer();
	private Map cache=new HashMap();
	private BloatUtil bloatUtil=new BloatUtil(new ClassFileLoader());
	
	public Db4oEnhancingClassloader(ClassLoader delegate) {
		super(new URL[]{},delegate);
	}

	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(cache.containsKey(name)) {
			return (Class)cache.get(name);
		}
		Class delegateClass = super.loadClass(name,resolve);
		if(mustDelegate(name)) {
			return delegateClass;
		}
		Class clazz=(Predicate.class.isAssignableFrom(delegateClass) ? findClass(name) : findRawClass(name));
		cache.put(clazz.getName(),clazz);
		if(resolve) {
			resolveClass(clazz);
		}
		return clazz;
	}

	private Class findRawClass(String className) throws ClassNotFoundException {
        try {
			String resourcePath=className.replace('.','/')+".class";
			InputStream resourceStream = getResourceAsStream(resourcePath);
			ByteArrayOutputStream rawByteStream=new ByteArrayOutputStream();
			byte[] buf=new byte[4096];
			int bytesread=0;
			while((bytesread=resourceStream.read(buf))>=0) {
				rawByteStream.write(buf,0,bytesread);
			}
			resourceStream.close();
			byte[] rawBytes=rawByteStream.toByteArray();
			return super.defineClass(className, rawBytes, 0, rawBytes.length);
		} catch (Exception exc) {
			throw new ClassNotFoundException(className,exc);
		}	
	}

	private boolean mustDelegate(String name) {
		return name.startsWith("java.")||name.startsWith("javax.")||name.startsWith("sun.")||(name.startsWith("com.db4o.")&&!name.startsWith("com.db4o.test."));
	}

	protected void bloat(ClassEditor ce) {
		try {
			Type type=ce.superclass();
			while(type!=null) {
				if(type.className().equals(Predicate.class.getName().replace('.','/'))) {
					enhancer.enhance(bloatUtil,ce,Predicate.PREDICATEMETHOD_NAME,this.getParent());
					return;
				}
				ClassInfo classInfo=getClassInfoLoader().loadClass(type.className());
				type=new ClassEditor(new CachingBloatContext(getClassInfoLoader(),new ArrayList(),false),classInfo).superclass();
			}
			//System.err.println("Bypassing "+ce.name());
		} catch (Exception exc) {
			throw new RuntimeException(exc.getMessage());
		}
	}
}
