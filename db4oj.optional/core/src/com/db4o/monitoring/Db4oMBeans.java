/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import java.io.*;

import javax.management.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.monitoring.cs.*;

/**
 * @exclude
 */
@decaf.Ignore
public class Db4oMBeans {
	
	private static final String MONITORING_DOMAIN_NAME = "com.db4o.monitoring";

	public static String mBeanIDForContainer(ObjectContainer container) {
		return container.toString();
	}

	public static String mBeanIDForPath(String path) {
		return mBeanIDForFile(new File(path));
	}

	public static String mBeanIDForFile(File file) {
		return file.getName();
	}
	
	public static ObjectName mBeanNameFor(Class<?> mbeanInterface, String name) {
		name = name.replaceAll("[:\\?\\*=,\"]", " ");
		final String nameSpec = MONITORING_DOMAIN_NAME + ":name=" + name + ",mbean=" + displayName(mbeanInterface);
//		System.err.println("NAME SPEC: " + nameSpec);
		try {
			return new ObjectName(nameSpec);
		} catch (MalformedObjectNameException e) {
			throw new IllegalStateException("'" + nameSpec + "' is not a valid name.", e);
		}
	}

	private static String displayName(Class<?> mbeanInterface) {
		String className = mbeanInterface.getSimpleName();
		if(! className.endsWith("MBean")){
			throw new IllegalArgumentException();
		}
		return className.substring(0, className.length() - "MBean".length());
	}
	
	static IO newIOStatsMBean(ObjectContainer container) {
		try {
			return new IO(container, IOMBean.class);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}

	public static Networking newClientNetworkingStatsMBean(ObjectContainer container) {
		try {
			return new Networking(container, NetworkingMBean.class);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}
	
	public static Networking newServerNetworkingStatsMBean(ObjectContainer container) {
		try {
			return new SynchronizedNetworking(container, NetworkingMBean.class);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}
	
	public static Queries newQueriesMBean(InternalObjectContainer container) {
		try {
			return new Queries(container, QueriesMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static com.db4o.monitoring.ReferenceSystem newReferenceSystemMBean(InternalObjectContainer container) {
		try {
			return new com.db4o.monitoring.ReferenceSystem(container, ReferenceSystemMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static NativeQueries newNativeQueriesMBean(InternalObjectContainer container) {
		try {
			return new NativeQueries(container, NativeQueriesMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static Freespace newFreespaceMBean(InternalObjectContainer container) {
		try {
			return new Freespace(container, FreespaceMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static ClientConnections newClientConnectionsMBean(ObjectContainer container) {
		try {
			return new ClientConnections(container, ClientConnectionsMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static ObjectLifecycle newObjectLifecycleMBean(ObjectContainer container) {
		try {
			return new ObjectLifecycle(container, ObjectLifecycleMBean.class);
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}
	
}
