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
		if(container instanceof LocalObjectContainer){
			return mBeanIDForPath(((LocalObjectContainer) container).fileName());
		}
		return container.toString();
	}

	public static String mBeanIDForPath(String path) {
		return mBeanIDForFile(new File(path));
	}

	public static String mBeanIDForFile(File file) {
		return file.getName();
	}
	
	public static ObjectName mBeanNameFor(Class<?> mbeanInterface, String name) {
		final String nameSpec = MONITORING_DOMAIN_NAME + ":name=\"" + name + "\",mbean=" + displayName(mbeanInterface);
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
	
	static IO newIOStatsMBean(String uri) {
		try {
			final ObjectName objectName = mBeanNameFor(IOMBean.class, mBeanIDForPath(uri));
			return new IO(objectName);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}

	public static Networking newClientNetworkingStatsMBean(ObjectContainer container) {
		try {
			final ObjectName objectName = mBeanNameFor(NetworkingMBean.class, mBeanIDForContainer(container));
			return new Networking(objectName);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}
	
	public static Networking newServerNetworkingStatsMBean(ObjectContainer container) {
		try {
			final ObjectName objectName = mBeanNameFor(NetworkingMBean.class, mBeanIDForContainer(container));
			return new SynchronizedNetworking(objectName);
		} catch (JMException e) {
			throw new Db4oException(e);
		}
	}
	
	public static Queries newQueriesMBean(InternalObjectContainer container) {
		try {
			return new Queries(mBeanNameFor(QueriesMBean.class, mBeanIDForContainer(container)));
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static com.db4o.monitoring.ReferenceSystem newReferenceSystemMBean(InternalObjectContainer container) {
		try {
			return new com.db4o.monitoring.ReferenceSystem(mBeanNameFor(ReferenceSystemMBean.class, mBeanIDForContainer(container)));
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static NativeQueries newNativeQueriesMBean(InternalObjectContainer container) {
		try {
			return new NativeQueries(mBeanNameFor(NativeQueriesMBean.class, mBeanIDForContainer(container)));
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static Freespace newFreespaceMBean(InternalObjectContainer container) {
		try {
			return new Freespace(mBeanNameFor(FreespaceMBean.class, mBeanIDForContainer(container)));
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}

	public static ClientConnections newClientConnectionsMBean(ObjectContainer container) {
		try {
			return new ClientConnections(mBeanNameFor(ClientConnectionsMBean.class, Db4oMBeans.mBeanIDForContainer(container)));
		} catch (JMException e) {
			throw new Db4oIllegalStateException(e);
		}
	}	
}
