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
		return new ObjectName("Db4oStatsAgent:name=" + mbeanInterface.getSimpleName());
	}
	
	static IOStats newIOStatsMBean(String uri) {
		try {
			final ObjectName objectName = mBeanNameFor(IOStatsMBean.class, uri);
			return new IOStats(objectName);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}	
}
