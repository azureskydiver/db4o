/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import com.db4o.*;
import com.db4o.reflect.*;

public class JdkReflector implements Reflector{
    
	private final ClassLoader _classLoader;

	private final ReflectArray _array;
	
	private final Hashtable4 _byClass;
	private final Hashtable4 _byName;
	
	private final Collection4 _collectionClasses;
	
	private final Collection4 _collectionUpdateDepths;
	
	
	public JdkReflector(ClassLoader classLoader){
		if(classLoader == null){
			throw new NullPointerException();
		}
		_classLoader = classLoader;
		_array = new JdkArray(this);
		_byClass = new Hashtable4(1);
		_byName = new Hashtable4(1);
		_collectionClasses = new Collection4();
		_collectionUpdateDepths = new Collection4();
	}
	
	public ReflectArray array(){
		return _array;
	}
	
	private ReflectClass addClass(String className, Class clazz) {
		JdkClass cClass = new JdkClass(this, clazz);
		_byClass.put(clazz, cClass);
		_byName.put(className, cClass);
		return cClass;
	}
	
	public boolean constructorCallsSupported(){
		return true;
	}
	
	public ReflectClass forClass(Class clazz){
		
		if(clazz == null){
			return null;
		}
		ReflectClass iClass = (ReflectClass)_byClass.get(clazz);
		if(iClass != null){
			return iClass;
		}
		return addClass(clazz.getName(), clazz);
	}
	
	public ReflectClass forName(String className) {
		ReflectClass iClass = (ReflectClass)_byName.get(className);
		if(iClass != null){
			return iClass;
		}
		Class clazz;
		try {
			clazz = _classLoader.loadClass(className);
		}
		catch(ClassNotFoundException exc) {
			clazz=null;
		}
		if(clazz == null){
			return null;
		}
		return addClass(className, clazz);
	}
	
	public ReflectClass forObject(Object a_object) {
		if(a_object == null){
			return null;
		}
		return forClass(a_object.getClass());
	}
	
	public boolean isCollection(ReflectClass candidate) {
		Iterator4 it = _collectionClasses.iterator();
		while(it.hasNext()){
			ReflectClass claxx = (ReflectClass)it.next();
			if(claxx.isAssignableFrom(candidate)){
				return true;
			}
		}
		return false;
	}

	public boolean methodCallsSupported(){
		return true;
	}
	
	public void registerCollection(Class clazz) {
		ReflectClass claxx = forClass(clazz);
		_collectionClasses.add(claxx);
	}
	
	public void registerCollectionUpdateDepth(Class clazz, int depth) {
		Object[] entry = new Object[]{forClass(clazz), new Integer(depth) };
		_collectionUpdateDepths.add(entry);
	}

	static Class[] toNative(ReflectClass[] claxx){
        Class[] clazz = null;
        if(claxx != null){
        	clazz = new Class[claxx.length];
        	for (int i = 0; i < claxx.length; i++) {
        		if(claxx[i] != null){
        			clazz[i] = ((JdkClass)claxx[i]).getJavaClass();
        		}
			}
        }
        return clazz;
	}
	
	public static ReflectClass[] toMeta(Reflector reflector, Class[] clazz){
        ReflectClass[] claxx = null;
        if(clazz != null){
        	claxx = new ReflectClass[clazz.length];
        	for (int i = 0; i < clazz.length; i++) {
        		if(clazz[i] != null){
        			claxx[i] = reflector.forClass(clazz[i]);
        		}
			}
        }
		return claxx;
	}

	public int collectionUpdateDepth(ReflectClass candidate) {
		Iterator4 i = _collectionUpdateDepths.iterator();
		while(i.hasNext()){
			Object[] entry = (Object[])i.next();
			ReflectClass claxx = (ReflectClass) entry[0];
			if(claxx.isAssignableFrom(candidate)){
				return ((Integer)entry[1]).intValue();
			}
		}
		return 2;
	}
}
