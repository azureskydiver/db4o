/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import javax.management.*;

import com.db4o.monitoring.*;

/**
 * @exclude
 */
@decaf.Ignore
public class ClientConnections extends MBeanRegistrationSupport implements ClientConnectionsMBean {

	public ClientConnections(ObjectName objectName) throws JMException {
		super(objectName);
	}

	public int getConnectedClientCount() {
		synchronized (_connectedClientsLock) {
			return _connectedClients;
		}
	}
	
	public void notifyClientConnected() {
		synchronized(_connectedClientsLock) {
			_connectedClients++;
		}
	}
	
	public void notifyClientDisconnected() {
		synchronized(_connectedClientsLock) {
			_connectedClients--;
		}
	}
	
	private int _connectedClients;	
	private final Object _connectedClientsLock = new Object();
	
}
