/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import com.db4o.Db4o;
import com.db4o.reflect.*;

public class CReflect implements IReflect{
	
	private final IArray i_array;
	
	public CReflect(){
		i_array = new CArray();
	}
	
	public IArray array(){
		return i_array;
	}
	
	public boolean constructorCallsSupported(){
		return true;
	}
	
	public IClass forName(String className) throws ClassNotFoundException{
		Class clazz = Db4o.classForName(className);
		if(clazz == null){
			return null;
		}
		return new CClass(clazz);
	}
	
	public boolean methodCallsSupported(){
		return true;
	}
}
