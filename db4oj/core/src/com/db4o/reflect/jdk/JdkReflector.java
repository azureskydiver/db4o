/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import com.db4o.reflect.*;

public class JdkReflector implements Reflector{
	
    private final JdkLoader _classLoader;
    private Reflector _parent;
    private ReflectArray _array;

	public JdkReflector(ClassLoader classLoader){
		this(new ClassLoaderJdkLoader(classLoader));
	}

	public JdkReflector(JdkLoader classLoader){
		_classLoader = classLoader;
	}
	
	public ReflectArray array(){
        if(_array == null){
            _array = new JdkArray(_parent);
        }
		return _array;
	}
	
	public boolean constructorCallsSupported(){
		return true;
	}
    
    public Object deepClone(Object context) {
        return new JdkReflector((JdkLoader)_classLoader.deepClone(context));
    }
	
	public ReflectClass forClass(Class clazz){
        return new JdkClass(_parent, clazz);
	}
	
	public ReflectClass forName(String className) {
		Class clazz = safeForName(_classLoader, className);
		if (clazz == null) {
			return null;
		}
		return new JdkClass(_parent, clazz);
	}
	
	private static Class safeForName(JdkLoader classLoader, String className) {
		try {
			return classLoader == null ? Class.forName(className) : classLoader.loadClass(className);
		} catch (Exception e) {
			// e.printStackTrace();
		} catch (LinkageError e) {
			// e.printStackTrace();
		}
		return null;
	}
	
	public ReflectClass forObject(Object a_object) {
		if(a_object == null){
			return null;
		}
		return _parent.forClass(a_object.getClass());
	}
	
	public boolean isCollection(ReflectClass candidate) {
		return false;
	}

	public boolean methodCallsSupported(){
		return true;
	}

    public void setParent(Reflector reflector) {
        _parent = reflector;
    }

    public static ReflectClass[] toMeta(Reflector reflector, Class[] clazz){
        ReflectClass[] claxx = null;
        if(clazz != null){
            claxx = new ReflectClass[clazz.length];
            for (int i = 0; i < clazz.length; i++) {
                if(clazz[i] != null){
                    claxx[i] = reflector.forClass(clazz[i]);
                }
            }
        }
        return claxx;
    }
    
    static Class[] toNative(ReflectClass[] claxx){
        Class[] clazz = null;
        if(claxx != null){
            clazz = new Class[claxx.length];
            for (int i = 0; i < claxx.length; i++) {
                clazz[i] = toNative(claxx[i]);
            }
        }
        return clazz;
    }
    
    public static Class toNative(ReflectClass claxx){
        if(claxx == null){
            return null;
        }
        if(claxx instanceof JdkClass){
            return ((JdkClass)claxx).getJavaClass();
        }
        ReflectClass d = claxx.getDelegate();
        if(d == claxx){
            return null;
        }
        return toNative(d);
    }

}
