/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.util.Hashtable;

import com.db4o.Db4o;
import com.db4o.reflect.*;

public class CReflect implements IReflect{
    
    private static CReflect reflect = null;
    
    public static CReflect getDefault() {
        if (reflect == null)
            reflect = new CReflect();
        return reflect;
    }
	
	private final IArray i_array;
	
	private final Hashtable _byClass;
	private final Hashtable _byName;
	
	public CReflect(){
		i_array = new CArray();
		_byClass = new Hashtable();
		_byName = new Hashtable();
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
		Class clazz = Db4o.classForName(className);
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

	public boolean methodCallsSupported(){
		return true;
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

	
	
}
