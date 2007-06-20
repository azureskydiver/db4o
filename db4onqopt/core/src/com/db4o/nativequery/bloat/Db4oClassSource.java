package com.db4o.nativequery.bloat;

import EDU.purdue.cs.bloat.file.ClassSource;

import com.db4o.reflect.Reflector;
import com.db4o.reflect.jdk.JdkReflector;

public class Db4oClassSource implements ClassSource {
	
	private Reflector _reflector;
	
	public Db4oClassSource(Reflector reflector) {
		_reflector = reflector;
	}
	
	public Class loadClass(String name) throws ClassNotFoundException {
		return JdkReflector.toNative(_reflector.forName(name));
	}

}
