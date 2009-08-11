/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import java.lang.management.*;

import javax.management.*;

import com.db4o.monitoring.internal.*;

@decaf.Ignore
class IO implements IOMBean {

	private TimedReading _numBytesReadPerSec = TimedReading.newPerSecond();
	
	private TimedReading _numBytesWrittenPerSec = TimedReading.newPerSecond();
	
	private TimedReading _numReadsPerSec = TimedReading.newPerSecond();
	
	private TimedReading _numWritesPerSec = TimedReading.newPerSecond();
	
	private TimedReading _numSyncsPerSec = TimedReading.newPerSecond();
	
	private ObjectName _objectName;
	
	public IO(ObjectName objectName) throws JMException {
		_objectName = objectName;
		register();
	}
	
	public double getBytesReadPerSecond() {
		return _numBytesReadPerSec.read();
	}

	public double getBytesWrittenPerSecond() {
		return _numBytesWrittenPerSec.read();
	}

	public double getReadsPerSecond() {
		return _numReadsPerSec.read();
	}

	public double getWritesPerSecond() {
		return _numWritesPerSec.read();
	}

	public double getSyncsPerSecond() {
		return _numSyncsPerSec.read();
	}

	public void notifyBytesRead(int numBytesRead) {
		_numBytesReadPerSec.add(numBytesRead);
		_numReadsPerSec.increment();
	}

	public void notifyBytesWritten(int numBytesWritten) {
		_numBytesWrittenPerSec.add(numBytesWritten);
		_numWritesPerSec.increment();
	}

	public void notifySync() {
		_numSyncsPerSec.increment();
	}

	public void unregister() {
		try {
			platformMBeanServer().unregisterMBean(_objectName);
		} catch (JMException e) {
			e.printStackTrace();
		}
	}
	
	private void register() throws InstanceAlreadyExistsException,
			MBeanRegistrationException, NotCompliantMBeanException {
		platformMBeanServer().registerMBean(this, _objectName);
	}

	private MBeanServer platformMBeanServer() {
		return ManagementFactory.getPlatformMBeanServer();
	}

}
