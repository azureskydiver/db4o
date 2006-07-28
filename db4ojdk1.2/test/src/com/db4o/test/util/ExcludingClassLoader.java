/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.util;

import java.net.*;
import java.util.*;

public class ExcludingClassLoader extends URLClassLoader {
	private List excludedNames;
	
	public ExcludingClassLoader(ClassLoader parent,List excludedNames) {
		super(new URL[]{},parent);
		this.excludedNames=excludedNames;
	}
	
	protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if(excludedNames.contains(name)) {
			throw new ClassNotFoundException(name);
		}
		return super.loadClass(name, resolve);
	}
	
	public static void main(String[] args) throws Exception {
		ClassLoader parent=ExcludingClassLoader.class.getClassLoader();
		String excName=ExcludingClassLoader.class.getName();
		List excluded=new ArrayList();
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
