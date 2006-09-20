/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.drs.inside;


public interface SimpleObjectContainer {

	public void activate(Object object);

	public void commit();

	public void delete(Object obj);

	public void deleteAllInstances(Class clazz);

	/**
	 * Will cascade to save the whole graph of objects
	 *
	 * @param o
	 */
	public void storeNew(Object o);

	/**
	 * It won't cascade. Use it with caution.
	 *
	 * @param o
	 */
	public void update(Object o);

}
