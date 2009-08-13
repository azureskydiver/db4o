/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import java.lang.management.*;

import javax.management.*;

@decaf.Ignore
class MBeanRegistrationSupport {

	protected ObjectName _objectName;

	public MBeanRegistrationSupport(ObjectName objectName) throws JMException {
		_objectName = objectName;
		register(this);
	}

	public void unregister() {
		try {
			platformMBeanServer().unregisterMBean(_objectName);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}

	private void register(Object mbeanInstance) throws InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException {
			platformMBeanServer().registerMBean(mbeanInstance, _objectName);
	}

	private MBeanServer platformMBeanServer() {
		return ManagementFactory.getPlatformMBeanServer();
	}

	protected Object objectName() {
		return _objectName;
	}

}
