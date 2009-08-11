/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import javax.management.*;

import com.db4o.ext.*;

/**
 * @exclude
 */
@decaf.Ignore
public class Db4oMBeans {
	
	public static ObjectName mBeanNameFor(Class<?> mbeanInterface, String uri)
		throws MalformedObjectNameException {
		return new ObjectName("com.db4o.monitoring:name=" + uri + ",mbean=" + displayName(mbeanInterface));
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
