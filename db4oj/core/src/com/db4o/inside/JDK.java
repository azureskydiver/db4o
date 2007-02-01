/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.types.*;


/**
 * @exclude
 */
public class JDK {
	
	Thread addShutdownHook(Runnable runnable){
		return null;
	}
	
	Db4oCollections collections(YapStream session){
	    return null;
	}
    
    Class constructorClass(){
        return null;
    }
	
	Object createReferenceQueue() {
		return null;
	}

    public Object createWeakReference(Object obj){
        return obj;
    }
    
	Object createYapRef(Object queue, ObjectReference ref, Object obj) {
		return null;
	}
	
    Object deserialize(byte[] bytes) {
    	throw new Db4oException(Messages.NOT_IMPLEMENTED);
    }

    public Config4Class extendConfiguration(ReflectClass clazz, Configuration config, Config4Class classConfig) {
    	return classConfig;
    }

    void forEachCollectionElement(Object obj, Visitor4 visitor) {
        if(! Deploy.csharp){
            Enumeration e = null;
            if (obj instanceof Hashtable) {
                e = ((Hashtable)obj).elements();
            } else if (obj instanceof Vector) {
                e = ((Vector)obj).elements();
            }
            if (e != null) {
                while (e.hasMoreElements()) {
                    visitor.visit(e.nextElement());
                }
            }
        }
	}
	
	String format(Date date, boolean showTime) {
		return date.toString();
	}
	
	Object getContextClassLoader(){
		return null;
	}

	Object getYapRefObject(Object obj) {
		return null;
	}
    
    boolean isCollectionTranslator(Config4Class config) {
        if(!Deploy.csharp){
            if (config != null) {
                ObjectTranslator ot = config.getTranslator();
                if (ot != null) {
                    return ot instanceof THashtable;
                }
            }
        }
        return false;
    }
    
   public boolean isConnected(Socket socket){
       return socket != null;
   }

	public int ver(){
	    return 1;
	}
	
	void killYapRef(Object obj){
		
	}
	
	synchronized void lockFile(Object file) {
	}
	
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
	boolean methodIsAvailable(String className, String methodName, Class[] params) {
    	return false;
    }

	void pollReferenceQueue(YapStream session, Object referenceQueue) {
		
	}
	
	public void registerCollections(GenericReflector reflector) {
		
	}
	
	void removeShutdownHook(Thread thread){
		
	}
	
	public Constructor serializableConstructor(Class clazz){
	    return null;
	}
	
    byte[] serialize(Object obj) throws Exception{
    	throw new Db4oException(Messages.NOT_IMPLEMENTED);
    }

	void setAccessible(Object accessibleObject) {
	}
    
    boolean isEnum(Reflector reflector, ReflectClass clazz) {
        return false;
    }
	
	synchronized void unlockFile(Object file) {
	}
    
    public Object weakReferenceTarget(Object weakRef){
        return weakRef;
    }
    
    public Reflector createReflector(Object classLoader) {
    	return null;
    }
}
