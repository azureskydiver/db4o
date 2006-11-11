/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.tools.defragment;

import com.db4o.*;

/**
 * Filter that accepts only YapClass instances whose corresponding Java class is
 * currently known.
 */
public class AvailableClassFilter implements YapClassFilter {
	private ClassLoader _loader;

	/**
	 * Will accept only classes that are known to the classloader that loaded
	 * this class.
	 */
	public AvailableClassFilter() {
		this(AvailableClassFilter.class.getClassLoader());
	}

	/**
	 * Will accept only classes that are known to the given classloader.
	 * 
	 * @param loader The classloader to check class names against
	 */
	public AvailableClassFilter(ClassLoader loader) {
		_loader = loader;
	}

	/**
	 * Will accept only classes whose corresponding platform class is known
	 * to the configured classloader.
	 * 
	 * @param yapClass The YapClass instance to be checked
	 * @return true if the corresponding platform class is known to the configured classloader, false otherwise
	 */
	public boolean accept(YapClass yapClass) {
		try {
			_loader.loadClass(yapClass.getName());
			return true;
		} catch (ClassNotFoundException exc) {
			return false;
		}
	}
}
