package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.CommunicationChannelControl;
import com.db4o.drs.versant.ipc.inband.*;

public class ObjectLifecycleMonitorNetworkFactory {
	
//	private static final ObjectLifecycleMonitorNetwork factory = new TcpCommunicationNetwork();
	private static final ObjectLifecycleMonitorNetwork factory = new InBandCommunicationNetwork();

	public static ObjectLifecycleMonitor newClient(final VodCobra cobra, final int senderId) {

		return factory.newClient(cobra, senderId);
	}

	public static CommunicationChannelControl prepareProviderCommunicationChannel(ObjectLifecycleMonitor provider, final Object lock, final VodCobra cobra, VodEventClient client,
			int senderId) {

		return factory.prepareCommunicationChannel(provider, lock, cobra, client, senderId);
	}
	

}
