/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.monitoring;


/**
 * JMX MBean for IO statistics.
 * 
 * @see MonitoredStorage
 */
@decaf.Ignore
public interface IOMBean {

	double getBytesReadPerSecond();
	double getBytesWrittenPerSecond();
	double getReadsPerSecond();
	double getWritesPerSecond();
	double getSyncsPerSecond();
	
}
