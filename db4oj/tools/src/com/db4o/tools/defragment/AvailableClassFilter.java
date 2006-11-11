/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;

public class AvailableClassFilter implements YapClassFilter {
	private ClassLoader _loader;
	
	public AvailableClassFilter(ClassLoader loader) {
		super();
		_loader = loader;
	}

	public boolean accept(YapClass yapClass) {
		try {
			_loader.loadClass(yapClass.getName());
			return true;
		} catch (ClassNotFoundException exc) {
			return false;
		}
	}
}
