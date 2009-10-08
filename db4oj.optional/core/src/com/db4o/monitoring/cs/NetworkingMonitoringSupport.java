/* Copyright (C) 2009  Versant Corp.  http://www.db4o.com */

package com.db4o.monitoring.cs;

import static com.db4o.foundation.Environments.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.cs.internal.config.*;
import com.db4o.events.*;
import com.db4o.foundation.network.*;
import com.db4o.internal.*;
import com.db4o.monitoring.*;

/**
 * publishes statistics about networking activities to JMX.
 */
@decaf.Ignore
public class NetworkingMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
		// registerClientConnectionsStatsMBean(my(ObjectServer.class));
	}

	public void prepare(Configuration configuration) {
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(configuration);
		Socket4Factory currentSocketFactory = networkConfig.socketFactory();
		networkConfig.socketFactory(new MonitoredSocket4Factory(currentSocketFactory));
	}
	
	private void registerClientConnectionsStatsMBean(ObjectServer server) {
		
		ObjectServerImpl serverImpl = (ObjectServerImpl) server;
		ObjectContainer objectContainer = serverImpl.objectContainer();
		
		final ClientConnections bean = Db4oMBeans.newClientConnectionsMBean(objectContainer);
		
		serverImpl.clientConnected().addListener(new EventListener4<ClientConnectionEventArgs>() { public void onEvent(Event4<ClientConnectionEventArgs> e, ClientConnectionEventArgs args) {
			bean.notifyClientConnected();
		}});
		
		serverImpl.clientDisconnected().addListener(new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
			bean.notifyClientDisconnected();
		}});
		
		serverImpl.closed().addListener(new EventListener4<ServerClosedEventArgs>() {
			public void onEvent(Event4<ServerClosedEventArgs> e,
					ServerClosedEventArgs args) {
				bean.unregister();
			}
		});
		
	}


}
