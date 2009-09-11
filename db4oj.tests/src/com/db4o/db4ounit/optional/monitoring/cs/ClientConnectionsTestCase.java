/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;

import com.db4o.cs.*;
import com.db4o.cs.internal.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.db4ounit.optional.monitoring.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.monitoring.*;
import com.db4o.monitoring.cs.*;

import db4ounit.*;

@decaf.Ignore
public class ClientConnectionsTestCase extends TestWithTempFile {

	private static final int PORT = 0xDB40;
	private static final String USER = "db4o";
	private static final String PASSWORD = "db4o";
	private ObjectServerImpl _server;
	private ClientConnections _bean;

	public void testConnectedClients() {
		
		for(int i=0; i < 3; i++) {
			Assert.areEqual(0, connectedClientCount());
			ExtObjectContainer client1 = openNewSession();
			Assert.areEqual(1, connectedClientCount(), "client1");
			ExtObjectContainer client2 = openNewSession();
			Assert.areEqual(2, connectedClientCount(), "client1 and client2");
			ensureClose(client1);
			Assert.areEqual(1, connectedClientCount(), "client2");
			ensureClose(client2);
			Assert.areEqual(0, connectedClientCount());
		}		
	}

	private void ensureClose(ExtObjectContainer client) {
		final BooleanByRef closeEventRaised = new BooleanByRef();
		
		_server.clientDisconnected().addListener(new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
			synchronized (closeEventRaised) {
				closeEventRaised.value = true;
				closeEventRaised.notifyAll();
			}
		}});
		
		synchronized (closeEventRaised) {
			client.close();
			while (!closeEventRaised.value) {
				try {
					closeEventRaised.wait();
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	private ExtObjectContainer openNewSession() {
		return (ExtObjectContainer) Db4oClientServer.openClient("localhost", PORT, USER, PASSWORD);
	}

	private long connectedClientCount() {
		MBeanProxy bean = new MBeanProxy(Db4oMBeans.mBeanNameFor(ClientConnectionsMBean.class, tempFile()));
		return bean.<Integer>getAttribute("ConnectedClientCount").longValue();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		_server = (ObjectServerImpl) Db4oClientServer.openServer(tempFile(), PORT);
		_server.grantAccess(USER, PASSWORD);
		_bean = Db4oMBeans.newClientConnectionsStatsMBean(_server);
	}

	public void tearDown() throws Exception {
		_bean.unregister();
		_server.close();
		
		super.tearDown();
	}	
}
