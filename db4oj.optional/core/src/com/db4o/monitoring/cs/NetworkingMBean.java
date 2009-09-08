/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

/**
 * JMX MBean for networking IO statistics.
 * 
 * @see MonitoredSocket4Factory
 */
@decaf.Ignore
public interface NetworkingMBean {
	
	double getBytesSentPerSecond();
	
}
