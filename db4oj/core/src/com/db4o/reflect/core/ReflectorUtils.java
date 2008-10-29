/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.reflect.core;

import com.db4o.reflect.*;


/**
 * @exclude
 */
public class ReflectorUtils {
	
	public static ReflectClass reflectClassFor(Reflector reflector, Object clazz) {
        
       if(clazz instanceof ReflectClass){
            return (ReflectClass)clazz;
        }
        
        if(clazz instanceof Class){
            return reflector.forClass((Class)clazz);
        }
        
        if(clazz instanceof String){
            return reflector.forName((String)clazz);
        }
        
        return reflector.forObject(clazz);
    }


}
