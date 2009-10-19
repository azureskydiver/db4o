/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import javax.management.*;

import com.db4o.*;
import com.db4o.monitoring.*;

/**
 * @exclude
 */
@decaf.Ignore
public class ClientConnections extends MBeanRegistrationSupport implements ClientConnectionsMBean {

	public ClientConnections(ObjectContainer db, Class<?> type) throws JMException {
		super(db, type);
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
