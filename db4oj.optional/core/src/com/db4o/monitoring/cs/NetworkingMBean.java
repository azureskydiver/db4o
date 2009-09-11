/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import com.db4o.monitoring.ResettableMBean;

/**
 * JMX MBean for networking IO statistics.
 * 
 * @see MonitoredSocket4Factory
 */
@decaf.Ignore
public interface NetworkingMBean extends ResettableMBean {
	
	double getBytesSentPerSecond();
	
	double getBytesReceivedPerSecond();
	
	double getMessagesSentPerSecond();

}
