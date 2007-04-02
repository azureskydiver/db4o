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
			throw new DatabaseFileLockedException(canonicalPath);
		}
		
		Object lock = null;
		try {
			Object channel = invoke(file, "getChannel", null, null);
			lock = invoke(channel, "tryLock", null, null);
		} catch (Throwable t) {
			Exceptions4.shouldNeverHappen();
		}
		if(lock == null){
			throw new DatabaseFileLockedException(canonicalPath);
		}
		fileLocks.put(canonicalPath, lock);
	}
	
	synchronized void unlockFile(String path, Object file) {
		if (fileLocks == null) {
			return;
		}
		Object fl = fileLocks.get(path);
		if (fl == null) {
			return;
		}
		try {
			invoke(fl, "release", null, null);
		} catch (Throwable e) {
			Exceptions4.shouldNeverHappen();
		}
		fileLocks.remove(path);
	}
	
	public Constructor serializableConstructor(Class clazz) {
		if (reflectionFactory == null) {
			try {
				initSerializableConstructor();
			} catch (Throwable t) {
				Platform4.callConstructorCheck = TernaryBool.YES;
				return null;
			}
		}
		try {
			return (Constructor) invoke(
					new Object[] { clazz, objectConstructor },
					reflectionFactory, factoryMethod);
		} catch (ReflectException e) {
			return null;
		}
	}
	
	void initSerializableConstructor() throws Throwable  {
        reflectionFactory = invoke(Platform4.REFLECTIONFACTORY, "getReflectionFactory", null,null, null);
        factoryMethod = getMethod(Platform4.REFLECTIONFACTORY, "newConstructorForSerialization", new Class[]{Class.class, Constructor.class});
        if(factoryMethod == null){
            throw new NoSuchMethodException();
        }
        Object.class.getDeclaredConstructor((Class[])null);
	}
	
	
	
	public int ver(){
	    return 4;
	}
	
}
