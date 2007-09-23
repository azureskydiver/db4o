/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.migration;

import java.io.*;

import com.db4o.foundation.*;

public class Db4oLibraryEnvironmentProvider {
	
	final Hashtable4 _environments = new Hashtable4();
	
	public Db4oLibraryEnvironment environmentFor(final String path)
			throws IOException {
		Db4oLibraryEnvironment existing = existingEnvironment(path);
		if (existing != null) return existing;
		return newEnvironment(path);
	}

	private Db4oLibraryEnvironment existingEnvironment(String path) {
		return (Db4oLibraryEnvironment) _environments.get(path);
	}

	private Db4oLibraryEnvironment newEnvironment(String path)
			throws IOException {
		Db4oLibraryEnvironment env = new Db4oLibraryEnvironment(new File(path));
		_environments.put(path, env);
		return env;
	}

}