package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.ClientChannelControl;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.ServerChannelControl;
import com.db4o.drs.versant.ipc.tcp.*;

public class ObjectLifecycleMonitorNetworkFactory {
	
	private static final ObjectLifecycleMonitorNetwork factory = new TcpCommunicationNetwork();
//	private static final ObjectLifecycleMonitorNetwork factory = new InBandCommunicationNetwork();

	public static ClientChannelControl newClient(final VodCobraFacade cobra, final int senderId) {

		return factory.newClient(cobra, senderId);
	}

	public static ServerChannelControl prepareProviderCommunicationChannel(ObjectLifecycleMonitor provider, final Object lock, final VodCobraFacade cobra, VodEventClient client,
			int senderId) {

		return factory.prepareCommunicationChannel(provider, lock, cobra, client, senderId);
	}
	

}
