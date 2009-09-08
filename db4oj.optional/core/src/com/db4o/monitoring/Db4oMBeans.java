/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import java.io.File;

import javax.management.JMException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.db4o.ObjectContainer;
import com.db4o.ext.Db4oException;
import com.db4o.monitoring.cs.*;

/**
 * @exclude
 */
@decaf.Ignore
public class Db4oMBeans {
	
	public static ObjectName mBeanNameFor(Class<?> mbeanInterface, String uri) {
		final String name = packageNameFor(mbeanInterface) + ":name=" + new File(uri).getName() + ",mbean=" + displayName(mbeanInterface);
		try {
			return new ObjectName(name);
		} catch (MalformedObjectNameException e) {
			throw new IllegalStateException("'" + name + "' is not a valid name.", e);
		}
	}

	private static String packageNameFor(Class<?> mbeanInterface) {
		String packageName = mbeanInterface.getPackage().getName();
		
		if (!isValidMonitoringPackage(packageName)) {
			throw new IllegalArgumentException("Package name for type '" +  mbeanInterface.getName() + "' is invalid");
		}
		
		return packageName;
	}

	private static boolean isValidMonitoringPackage(String packageName) {
		final String monitoringPackageName = "com.db4o.monitoring";
		return packageName.startsWith(monitoringPackageName + ".") || packageName.equals(monitoringPackageName);
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

	public static Networking newNetworkingStatsMBean(ObjectContainer container) {
		try {
			final ObjectName objectName = mBeanNameFor(NetworkingMBean.class, container.toString());
			return new Networking(objectName);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}	
}
