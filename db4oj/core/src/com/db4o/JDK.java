/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;
import java.lang.reflect.*;

import com.db4o.types.*;

/**
 * @exclude
 */
public class JDK {
	
	Thread addShutdownHook(Runnable a_runnable){
		return null;
	}
	
	int collectionUpdateDepth(Class a_class){
		return 0; 
	}
	
	Db4oCollections collections(YapStream a_stream){
	    return null;
	}
	
	Object createReferenceQueue() {
		return null;
	}

	YapRef createYapRef(Object a_queue, YapObject a_yapObject, Object a_object) {
		return null;
	}

	void flattenCollection2(Object a_object, final com.db4o.Collection4 col) {
		
	}

	void forEachCollectionElement(Object a_object, Visitor4 a_visitor) {
	}
	
	ClassLoader getContextClassLoader(){
		return null;
	}

	Object getYapRefObject(Object a_object) {
		return null;
	}

	boolean isCollection(Class a_class) {
		return false;
	}
	
	public int ver(){
	    return 1;
	}
	
	void killYapRef(Object obj){
		
	}
	
	synchronized void lock(RandomAccessFile file) {
	}
	
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
	boolean methodIsAvailable(
            String className,
            String methodName,
            Class[] params) {
    	return false;
    }

	void pollReferenceQueue(YapStream a_stream, Object a_referenceQueue) {
	}
	
	void removeShutdownHook(Thread a_thread){
		
	}
	
	Constructor serializableConstructor(Class clazz){
	    return null;
	}

	void setAccessible(Object a_accessible) {
	}
    
    boolean storeStaticFieldValues(Class clazz) {
        return false;
    }
	
	synchronized void unlock(RandomAccessFile file) {
	}

}
