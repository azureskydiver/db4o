/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

/**
 * JMX MBean for tracking client connections.
 * 
 * @since 7.12
 */
public interface ClientConnectionsMBean {
	int getConnectedClientCount();
}
