/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;

/**
 * JMX MBean for IO statistics.
 * 
 * @see MonitoredStorage
 */
@decaf.Ignore
public interface IOStatsMBean {

	double getNumBytesReadPerSecond();
	double getNumBytesWrittenPerSecond();
	double getNumReadsPerSecond();
	double getNumWritesPerSecond();
	double getNumSyncsPerSecond();
	double getNumSeeks();
	
}
