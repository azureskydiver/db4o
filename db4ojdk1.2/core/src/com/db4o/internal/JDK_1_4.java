/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.db4o.ext.*;
import com.db4o.internal.*;

class JDK_1_4 extends JDK_1_3 {
	
	private Hashtable fileLocks;
	
	private Object reflectionFactory;
	private Constructor objectConstructor;
	private Method factoryMethod;
	
	synchronized void lockFile(Object file) {
		Object channel = invoke(file, "getChannel", null, null);
		Object fl = invoke(channel, "tryLock", null, null); 
		if(fl == null){
			throw new DatabaseFileLockedException();
		}
		if(fileLocks == null){
			fileLocks = new Hashtable();
		}
		fileLocks.put(file, fl);
	}
	
	synchronized void unlockFile(Object file) {
		if(fileLocks != null){
			Object fl = fileLocks.get(file);
			if(fl != null){
			    invoke(fl, "release", null, null); 
				fileLocks.remove(file);
			}
		}
	}
	
	public Constructor serializableConstructor(Class clazz){
	    if(reflectionFactory == null){
	        if(! initSerializableConstructor()){
	            Platform4.callConstructorCheck = Const4.YES;
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
        reflectionFactory = invoke(Platform4.REFLECTIONFACTORY, "getReflectionFactory", null,null, null);
        if(reflectionFactory == null){
            return false;
        }
        factoryMethod = getMethod(Platform4.REFLECTIONFACTORY, "newConstructorForSerialization", new Class[]{Class.class, Constructor.class});
        if(factoryMethod == null){
            return false;
        }
        try{
            objectConstructor = Object.class.getDeclaredConstructor((Class[])null);
        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
	}
	
	
	
	public int ver(){
	    return 4;
	}
	
}
