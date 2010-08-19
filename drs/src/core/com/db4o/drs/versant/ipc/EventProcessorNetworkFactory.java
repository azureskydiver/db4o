package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.tcp.*;

public class EventProcessorNetworkFactory {
	
	private static final EventProcessorNetwork factory = new TcpCommunicationNetwork();
//	private static final EventProcessorNetwork factory = new InBandCommunicationNetwork();

	public static ProviderSideCommunication newClient(final VodCobra cobra, final int senderId) {

		return factory.newClient(cobra, senderId);
	}

	public static Thread prepareProviderCommunicationChannel(ProviderSideCommunication provider, final Object lock, final VodCobra cobra, VodEventClient client,
			int senderId) {

		return factory.prepareProviderCommunicationChannel(provider, lock, cobra, client, senderId);
	}
	

}
