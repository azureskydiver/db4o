/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import com.db4o.*;
import com.db4o.reflect.*;

public class CReflect implements IReflect{
    
	private final ClassLoader _classLoader;

	private final IArray i_array;
	
	private final Hashtable4 _byClass;
	private final Hashtable4 _byName;
	
	private final Collection4 _collectionClasses;
	
	private final Collection4 _collectionUpdateDepths;
	
	
	public CReflect(ClassLoader classLoader){
		if(classLoader == null){
			throw new NullPointerException();
		}
		_classLoader = classLoader;
		i_array = new CArray();
		_byClass = new Hashtable4(1);
		_byName = new Hashtable4(1);
		_collectionClasses = new Collection4();
		_collectionUpdateDepths = new Collection4();
	}
	
	public IArray array(){
		return i_array;
	}
	
	private IClass addClass(String className, Class clazz) {
		CClass cClass = new CClass(this, clazz);
		_byClass.put(clazz, cClass);
		_byName.put(className, cClass);
		return cClass;
	}
	
	public boolean constructorCallsSupported(){
		return true;
	}
	
	public IClass forClass(Class clazz){
		
		if(clazz == null){
			return null;
		}
		IClass iClass = (IClass)_byClass.get(clazz);
		if(iClass != null){
			return iClass;
		}
		return addClass(clazz.getName(), clazz);
	}
	
	public IClass forName(String className) throws ClassNotFoundException{
		IClass iClass = (IClass)_byName.get(className);
		if(iClass != null){
			return iClass;
		}
		Class clazz = _classLoader.loadClass(className);
		if(clazz == null){
			return null;
		}
		return addClass(className, clazz);
	}
	
	public IClass forObject(Object a_object) {
		if(a_object == null){
			return null;
		}
		return forClass(a_object.getClass());
	}

	public boolean isCollection(IClass candidate) {
		Iterator4 it = _collectionClasses.iterator();
		while(it.hasNext()){
			IClass claxx = (IClass)it.next();
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
		IClass claxx = forClass(clazz);
		_collectionClasses.add(claxx);
	}
	
	public void registerCollectionUpdateDepth(Class clazz, int depth) {
		Object[] entry = new Object[]{forClass(clazz), new Integer(depth) };
		_collectionUpdateDepths.add(entry);
	}

	static Class[] toNative(IClass[] claxx){
        Class[] clazz = null;
        if(claxx != null){
        	clazz = new Class[claxx.length];
        	for (int i = 0; i < claxx.length; i++) {
        		if(claxx[i] != null){
        			clazz[i] = ((CClass)claxx[i]).getJavaClass();
        		}
			}
        }
        return clazz;
	}
	
	public static IClass[] toMeta(IReflect reflector, Class[] clazz){
        IClass[] claxx = null;
        if(clazz != null){
        	claxx = new IClass[clazz.length];
        	for (int i = 0; i < clazz.length; i++) {
        		if(clazz[i] != null){
        			claxx[i] = reflector.forClass(clazz[i]);
        		}
			}
        }
		return claxx;
	}

	public int collectionUpdateDepth(IClass candidate) {
		Iterator4 i = _collectionUpdateDepths.iterator();
		while(i.hasNext()){
			Object[] entry = (Object[])i.next();
			IClass claxx = (IClass) entry[0];
			if(claxx.isAssignableFrom(candidate)){
				return ((Integer)entry[1]).intValue();
			}
		}
		return 2;
	}
}
