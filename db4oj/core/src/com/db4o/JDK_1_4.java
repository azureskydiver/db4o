/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.db4o.ext.*;

class JDK_1_4 extends JDK_1_3 {
	
	private Hashtable fileLocks;
	
	private Object reflectionFactory;
	private Constructor objectConstructor;
	private Method factoryMethod;
	
	synchronized void lock(RandomAccessFile file) {
		Object channel = Reflection4.invoke(file, "getChannel", null, null);
		Object fl = Reflection4.invoke(channel, "tryLock", null, null); 
		if(fl == null){
			throw new DatabaseFileLockedException();
		}
		if(fileLocks == null){
			fileLocks = new Hashtable();
		}
		fileLocks.put(file, fl);
	}
	
	synchronized void unlock(RandomAccessFile file) {
		if(fileLocks != null){
			Object fl = fileLocks.get(file);
			if(fl != null){
			    Reflection4.invoke(fl, "release", null, null); 
				fileLocks.remove(file);
			}
		}
	}
	
	Constructor serializableConstructor(Class clazz){
	    if(reflectionFactory == null){
	        if(! initSerializableConstructor()){
	            Platform.noConstructorCheck = Platform.DONT_USE;
	            return null;
	        }
	    }
	    try{
	        return (Constructor) factoryMethod.invoke(reflectionFactory, new Object[]{clazz, objectConstructor});
	    }catch(Exception e){
	    }
	    return null;
	}
	
	boolean initSerializableConstructor(){
        reflectionFactory = Reflection4.invoke(Platform.REFLECTIONFACTORY, "getReflectionFactory", null,null, null);
        if(reflectionFactory == null){
            return false;
        }
        factoryMethod = Reflection4.method(Platform.REFLECTIONFACTORY, "newConstructorForSerialization", new Class[]{Class.class, Constructor.class});
        if(factoryMethod == null){
            return false;
        }
        try{
            objectConstructor = Object.class.getDeclaredConstructor(null);
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
	}
	
	
	
	public int ver(){
	    return 4;
	}
	
}
