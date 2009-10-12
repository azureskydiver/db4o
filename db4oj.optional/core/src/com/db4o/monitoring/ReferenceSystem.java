/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.monitoring;

import javax.management.*;

/**
 * @exclude
 */
@decaf.Ignore
public class ReferenceSystem extends MBeanRegistrationSupport implements ReferenceSystemMBean, ReferenceSystemListener {
	
	private int _objectReferenceCount;

	public ReferenceSystem(ObjectName objectName) throws JMException {
		super(objectName);
	}

	public int getObjectReferenceCount() {
		return _objectReferenceCount;
	}
	
	public void notifyReferenceCountChanged(int changedBy) {
		_objectReferenceCount += changedBy;
	}

}
