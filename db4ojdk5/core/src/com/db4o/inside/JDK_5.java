/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import java.net.*;

import com.db4o.config.*;
import com.db4o.config.annotations.reflect.ConfigurationIntrospector;
import com.db4o.ext.*;
import com.db4o.inside.*;
import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;

class JDK_5 extends JDK_1_4 {

	private static final String ENUM_CLASSNAME = "java.lang.Enum";

	private static ReflectClass enumClass;

	public Config4Class extendConfiguration(ReflectClass clazz,
			Configuration config, Config4Class classConfig) {
		Class javaClazz = JdkReflector.toNative(clazz);
		if(javaClazz==null) {
			return classConfig;
		}
		try {
			ConfigurationIntrospector instrospetor = new ConfigurationIntrospector(javaClazz, config, classConfig);
			return instrospetor.apply();
		} catch (Exception exc) {
			throw new Db4oException(exc);
		}
	}
    
    public boolean isConnected(Socket socket){
        if(socket == null){
            return false;
        }
        if(! socket.isConnected() ){
            return false;
        }
        return ! socket.isClosed();
    }

	boolean isEnum(Reflector reflector, ReflectClass claxx) {

		if (claxx == null) {
			return false;
		}

		if (enumClass == null) {
			try {
				enumClass = reflector.forClass(Class.forName(ENUM_CLASSNAME));
			} catch (ClassNotFoundException e) {
				return false;
			}
		}

		return enumClass.isAssignableFrom(claxx);
	}

}
