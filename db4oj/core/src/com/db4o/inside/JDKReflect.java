/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.inside;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.db4o.*;
import com.db4o.reflect.Reflector;
import com.db4o.reflect.generic.*;
import com.db4o.reflect.jdk.JdkReflector;


/**
 * 
 * package and class name are hard-referenced in JavaOnly#jdk()
 * 
 * TODO: may need to use this on instead of JDK on .NET. Check!
 * 
 */
class JDKReflect extends JDK {
    Class constructorClass(){
        return Constructor.class;
    }
    
    Object deserialize(byte[] bytes) {
        try {
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        } catch (Exception e) {
        }
        return null;
    }
    
	String format(Date date, boolean showTime) {
        String fmt = "yyyy-MM-dd";
        if (showTime) {
            fmt += " HH:mm:ss";
        }
        return new SimpleDateFormat(fmt).format(date);
	}
	
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    final boolean methodIsAvailable(
        String className,
        String methodName,
        Class[] params) {
    	
        try {
        	
            Class clazz = Class.forName(className);
            if (clazz.getMethod(methodName, params) !=null) {
                return true;
            }
            return false;
        } catch (Throwable t) {
        }
        return false;
    }
    
    public static Object invoke (Class clazz, String methodName, Class[] paramClasses, Object[] params){
        return invoke(clazz.getName(), methodName, paramClasses, params, null);
    }
    
    
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    public static Object invoke (Object obj, String methodName, Class[] paramClasses, Object[] params){
        return invoke(obj.getClass().getName(), methodName, paramClasses, params, obj );
    }
    
    /**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    public static Object invoke (String className, String methodName, Class[] paramClasses, Object[] params, Object onObject){
        try {
                Method method = getMethod(className, methodName, paramClasses);
                return method.invoke(onObject, params);
            } catch (Throwable t) {
            }
        return null;
    }

    /**
     * calling this "method" will break C# conversion with the old converter
     * 
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
     */
    public static Method getMethod(String className, String methodName, Class[] paramClasses) {
        try {
            Class clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, paramClasses);
            return method;
        } catch (Throwable t) {
        }
        return null;
    }
    
    public void registerCollections(GenericReflector reflector) {
        if(! Deploy.csharp){
            reflector.registerCollection(java.util.Vector.class);
            reflector.registerCollection(java.util.Hashtable.class);
            reflector.registerCollectionUpdateDepth(java.util.Hashtable.class, 3);
        }
    }
    
    byte[] serialize(Object obj) throws Exception{
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        new ObjectOutputStream(byteStream).writeObject(obj);
        return byteStream.toByteArray();
    }

    public Reflector createReflector(Object classLoader) {
    	if(classLoader==null) {
            classLoader=getContextClassLoader();
            
            // FIXME: The new reflector does not like the ContextCloader at all.
            //        Resolve hierarchies.
            
            // if (cl == null || classloaderName.indexOf("eclipse") >= 0) {
                classLoader= Db4o.class.getClassLoader();
            // }
    	}
    	return new JdkReflector((ClassLoader)classLoader);
    }
}
