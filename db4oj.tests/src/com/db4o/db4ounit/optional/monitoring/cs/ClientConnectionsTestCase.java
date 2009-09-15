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
import db4ounit.extensions.fixtures.*;

@decaf.Ignore
public class ClientConnectionsTestCase extends TestWithTempFile implements OptOutAllButNetworkingCS {

	private static final int PORT = 0xDB40;
	private static final String USER = "db4o";
	private static final String PASSWORD = "db4o";

	public void testConnectedClients() {
		for(int i=0; i < 5; i++) {
			Assert.areEqual(0, connectedClientCount(), "No client yet.");
			ExtObjectContainer client1 = openNewSession();
			Assert.areEqual(1, connectedClientCount(), "client1:" + i);
			ExtObjectContainer client2 = openNewSession();
			Assert.areEqual(2, connectedClientCount(), "client1 and client2: " + i);
			ensureClose(client1);
			Assert.areEqual(1, connectedClientCount(), "client2: " + i);
			ensureClose(client2);
			Assert.areEqual(0, connectedClientCount(), "" + i);
		}		
	}

	private void ensureClose(ExtObjectContainer client) {
		synchronized (_closeEventRaised) {
			_closeEventRaised.value = false;
			client.close();
			while (!_closeEventRaised.value) {
				try {
					_closeEventRaised.wait();
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
		
		// We depend on the order of client connection/disconnection event firing.
		// We want the listener in the test to be notified before the one in the bean.
		_listener = registerCloseEventNotification();
		_bean = Db4oMBeans.newClientConnectionsStatsMBean(_server);
	}

	private EventListener4<StringEventArgs> registerCloseEventNotification() {
		EventListener4<StringEventArgs> listener = new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
			synchronized (_closeEventRaised) {
				_closeEventRaised.value = true;
				_closeEventRaised.notifyAll();
			}
		}};		
		_server.clientDisconnected().addListener(listener);
		return listener;
	}
	
	public void tearDown() throws Exception {
		_server.clientDisconnected().removeListener(_listener);
		_bean.unregister();
		_server.close();
		
		super.tearDown();
	}
	
	final BooleanByRef _closeEventRaised = new BooleanByRef();	
	private EventListener4<StringEventArgs> _listener;
	private ObjectServerImpl _server;
	private ClientConnections _bean;
}
