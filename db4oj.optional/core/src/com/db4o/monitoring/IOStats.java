/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

import java.lang.management.*;

import javax.management.*;

import com.db4o.monitoring.internal.*;

@decaf.Ignore
class IOStats implements IOStatsMBean {

	private TimedReading[] _readings;

	private ObjectName _objectName;
	
	private final static int NUM_READ_BYTES_IDX = 0;
	private final static int NUM_WRITTEN_BYTES_IDX = 1;
	private final static int NUM_READS_IDX = 2;
	private final static int NUM_WRITES_IDX = 3;
	private final static int NUM_FLUSHES_IDX = 4;
	private final static int NUM_SEEKS_IDX = 5;
	
	public IOStats(ObjectName objectName) throws JMException {
		_objectName = objectName;
		_readings = new TimedReading[6];
		for (int readingIdx = 0; readingIdx < _readings.length; readingIdx++) {
			_readings[readingIdx] = new TimedReading(1000);
		}
		register();
	}
	
	public double getNumBytesReadPerSecond() {
		return _readings[NUM_READ_BYTES_IDX].read();
	}

	public double getNumBytesWrittenPerSecond() {
		return _readings[NUM_WRITTEN_BYTES_IDX].read();
	}

	public double getNumReadsPerSecond() {
		return _readings[NUM_READS_IDX].read();
	}

	public double getNumWritesPerSecond() {
		return _readings[NUM_WRITES_IDX].read();
	}

	public double getNumSyncsPerSecond() {
		return _readings[NUM_FLUSHES_IDX].read();
	}

	public double getNumSeeks() {
		return _readings[NUM_SEEKS_IDX].read();
	}

	public void notifyBytesRead(int numBytesRead) {
		_readings[NUM_READ_BYTES_IDX].add(numBytesRead);
		_readings[NUM_READS_IDX].add(1);
		_readings[NUM_SEEKS_IDX].add(1);
	}

	public void notifyBytesWritten(int numBytesWritten) {
		_readings[NUM_WRITTEN_BYTES_IDX].add(numBytesWritten);
		_readings[NUM_WRITES_IDX].add(1);
		_readings[NUM_SEEKS_IDX].add(1);
	}

	public void notifyFlush() {
		_readings[NUM_FLUSHES_IDX].add(1);
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
