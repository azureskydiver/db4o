/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;

class JDK_1_4 extends JDK_1_3 {
	
	private Hashtable fileLocks;
	
	private Object reflectionFactory;
	private Constructor objectConstructor;
	private Method factoryMethod;
	
	synchronized void lockFile(String path,Object file) throws IOException {
		// Conversion to canonical is already done by RandomAccessFileAdapter, but it's probably
		// not safe to rely on that for other file-based adapters.
		String canonicalPath=new File(path).getCanonicalPath();
		if(fileLocks == null){
			fileLocks = new Hashtable();
		}
		if(fileLocks.containsKey(canonicalPath)) {
			throw new DatabaseFileLockedException();
		}
		Object channel = invoke(file, "getChannel", null, null);
		Object fl = invoke(channel, "tryLock", null, null); 
		if(fl == null){
			throw new DatabaseFileLockedException();
		}
		fileLocks.put(canonicalPath, fl);
	}
	
	synchronized void unlockFile(String path,Object file) {
		if(fileLocks != null){
			Object fl = fileLocks.get(path);
			if(fl != null){
			    invoke(fl, "release", null, null); 
				fileLocks.remove(path);
			}
		}
	}
	
	public Constructor serializableConstructor(Class clazz){
	    if(reflectionFactory == null){
	        if(! initSerializableConstructor()){
	            Platform4.callConstructorCheck = TernaryBool.YES;
	            return null;
	        }
	    }
	    return (Constructor) invoke(new Object[]{clazz, objectConstructor}, reflectionFactory, factoryMethod);
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
