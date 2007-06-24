package com.db4o.nativequery.bloat;

import EDU.purdue.cs.bloat.file.*;

import com.db4o.nativequery.*;

public class Db4oClassSource implements ClassSource {
	
	private NativeClassFactory _classFactory;
	
	public Db4oClassSource(NativeClassFactory classFactory) {
		_classFactory = classFactory;
	}
	
	public Class loadClass(String name) throws ClassNotFoundException {
		return _classFactory.forName(name);
	}

}
