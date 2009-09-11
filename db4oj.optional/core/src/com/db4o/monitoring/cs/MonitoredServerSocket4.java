/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.monitoring.cs;

import static com.db4o.foundation.Environments.my;

import java.io.*;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.foundation.network.*;
import com.db4o.monitoring.*;

@decaf.Ignore
class MonitoredServerSocket4 extends ServerSocket4Decorator {
	public MonitoredServerSocket4(ServerSocket4 serverSocket) {
		super(serverSocket);
	}

	public Socket4 accept() throws IOException {
		return new MonitoredSocket4(_serverSocket.accept(), bean());
	}
	
	Networking bean() {
		if (_bean == null) {
			_bean = Db4oMBeans.newNetworkingStatsMBean(my(ObjectContainer.class));			
			unregisterBeanOnServerClose(_bean);			
		}
		return _bean;
	}

	private void unregisterBeanOnServerClose(final Networking bean) {
		EventRegistry events = EventRegistryFactory.forObjectContainer(my(ObjectContainer.class));
		events.closing().addListener(new EventListener4<ObjectContainerEventArgs>() { public void onEvent(Event4<ObjectContainerEventArgs> e, ObjectContainerEventArgs args) {
			bean.unregister();
		}});
	}
	
	private Networking _bean;	
}