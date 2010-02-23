/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.cs.monitoring;

/**
 * @exclude
 * JMX MBean for tracking client connections.
 * 
 * @since 7.12
 */
@decaf.Ignore
public interface ClientConnectionsMBean {
	int getConnectedClientCount();
}