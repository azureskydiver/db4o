/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;

/**
 * root of the reflection implementation API.
 * <br><br>The open reflection interface is supplied to allow to implement
 * reflection functionality on JDKs that do not come with the
 * java.lang.reflect.* package.<br><br>
 * See the code in com.db4o.samples.reflect for a reference implementation
 * that uses java.lang.reflect.*.
 * <br><br>
 * Use {@link com.db4o.config.Configuration#reflectWith Db4o.configure().reflectWith(IReflect reflector)}
 * to register the use of your implementation before opening database
 * files.
 */
public interface IReflect {
	
	
	/**
	 * returns an IArray object, the equivalent to java.lang.reflect.Array.
	 */
	public IArray array();
	
	/**
	 * specifiy whether parameterized Constructors are supported.
	 * <br><br>The support of Constructors is optional. If Constructors
	 * are not supported, every persistent class needs a public default
	 * constructor with zero parameters.
	 */
	public boolean constructorCallsSupported();
	
	
	
	/**
	 * returns an IClass class reflector for a class name.
	 */
	public IClass forName(String className) throws ClassNotFoundException;
	
	/**
	 * returns an IClass for a Class
	 */
	public IClass forClass(Class clazz);
	
	
	/**
	 * returns an IClass for an object
	 */
	public IClass forObject(Object a_object);
	
	
	public boolean isCollection(IClass claxx);
	
	
	public void registerCollection(Class clazz);
	
	public void registerCollectionUpdateDepth(Class clazz, int depth);
	
	public int collectionUpdateDepth(IClass claxx);
	
	
}
