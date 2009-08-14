/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import java.io.*;

import javax.management.*;

import com.db4o.ext.*;

/**
 * @exclude
 */
@decaf.Ignore
public class Db4oMBeans {
	
	public static ObjectName mBeanNameFor(Class<?> mbeanInterface, String uri) {
		final String name = "com.db4o.monitoring:name=" + new File(uri).getName() + ",mbean=" + displayName(mbeanInterface);
		try {
			return new ObjectName(name);
		} catch (MalformedObjectNameException e) {
			throw new IllegalStateException("'" + name + "' is not a valid name.", e);
		}
	}

	private static String displayName(Class<?> mbeanInterface) {
		String className = mbeanInterface.getSimpleName();
		if(! className.endsWith("MBean")){
			throw new IllegalArgumentException();
		}
		return className.substring(0, className.length() - "MBean".length());
	}
	
	static IO newIOStatsMBean(String uri) {
		try {
			final ObjectName objectName = mBeanNameFor(IOMBean.class, uri);
			return new IO(objectName);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}	
}
