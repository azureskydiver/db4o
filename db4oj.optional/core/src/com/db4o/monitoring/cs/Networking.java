/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import javax.management.JMException;
import javax.management.ObjectName;

import com.db4o.monitoring.MBeanRegistrationSupport;
import com.db4o.monitoring.internal.TimedReading;

@decaf.Ignore
public class Networking extends MBeanRegistrationSupport implements NetworkingMBean {
	
	public Networking(ObjectName objectName) throws JMException {
		super(objectName);
	}

	public double getBytesSentPerSecond() {
		return bytesSent().read();
	}

	public void notifyWrite(int count) {
		bytesSent().incrementBy(count);
	}
	
	private TimedReading bytesSent() {
		if (_bytesSent == null) {
			_bytesSent = TimedReading.newPerSecond();
		}
		
		return _bytesSent;
	}
	
	@Override
	public String toString() {
		return _objectName.toString();
	}
	
	private TimedReading _bytesSent;
	
}
