/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.util;

import java.net.*;

import com.db4o.foundation.*;

public class ExcludingClassLoader extends URLClassLoader {
	private Collection4 _excludedNames;
	
	public ExcludingClassLoader(ClassLoader parent,Collection4 excludedNames) {
		super(new URL[]{},parent);
		this._excludedNames=excludedNames;
	}

	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(_excludedNames.contains(name)) {
			throw new ClassNotFoundException(name);
		}
		return super.loadClass(name, resolve);
	}
	
	public static void main(String[] args) throws Exception {
		ClassLoader parent=ExcludingClassLoader.class.getClassLoader();
		String excName=ExcludingClassLoader.class.getName();
		Collection4 excluded=new Collection4();
		ClassLoader incLoader=new ExcludingClassLoader(parent,excluded);
		System.out.println(incLoader.loadClass(excName));
		excluded.add(excName);
		try {
			System.out.println(incLoader.loadClass(excName));
		}
		catch(ClassNotFoundException exc) {
			System.out.println("Ok, not found.");
		}
	}
}
