/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.db4o.Db4o;
import com.db4o.Deploy;
import com.db4o.reflect.Reflector;
import com.db4o.reflect.generic.GenericReflector;
import com.db4o.reflect.jdk.JdkReflector;


/**
 * 
 * package and class name are hard-referenced in JavaOnly#jdk()
 * 
 * TODO: may need to use this on instead of JDK on .NET. Check!
 * @sharpen.ignore
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
    final boolean methodIsAvailable(String className, String methodName,
			Class[] params) {
		return getMethod(className, methodName, params) != null;
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
    public static Object invoke(String className, String methodName,
			Class[] paramClasses, Object[] params, Object onObject) {
		Method method = getMethod(className, methodName, paramClasses);
		return invoke(params, onObject, method);
	}

	public static Object invoke(Object[] params, Object onObject, Method method) {
		if(method == null) {
			return null;
		}
		try {
			return method.invoke(onObject, params);
		} catch (IllegalArgumentException e) {
			// e.printStackTrace();
		} catch (IllegalAccessException e) {
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// e.printStackTrace();
		} catch (ExceptionInInitializerError e) {
			// e.printStackTrace();
		}
		return null;
	}

    /**
	 * calling this "method" will break C# conversion with the old converter
	 * 
	 * use for system classes only, since not ClassLoader or Reflector-aware
	 */
    public static Method getMethod(String className, String methodName,
			Class[] paramClasses) {
		Class clazz = ReflectPlatform.forName(className);
		if (clazz == null) {
			return null;
		}
		try {
			return clazz.getMethod(methodName, paramClasses);
		} catch (SecurityException e) {
			// e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
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
