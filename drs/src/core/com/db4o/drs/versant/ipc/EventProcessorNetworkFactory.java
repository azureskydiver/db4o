package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.EventProcessorNetwork.ClientChannelControl;
import com.db4o.drs.versant.ipc.EventProcessorNetwork.ServerChannelControl;
import com.db4o.drs.versant.ipc.inband.*;
import com.db4o.drs.versant.ipc.tcp.*;

public class EventProcessorNetworkFactory {
	
	public static final boolean USE_IN_BAND_COMMUNICATION = false;
	
	private static final EventProcessorNetwork factory = USE_IN_BAND_COMMUNICATION ? new InBandCommunicationNetwork() : new TcpCommunicationNetwork();

	public static ClientChannelControl newClient(VodDatabase vod) {

		return factory.newClient(vod);
	}

	public static ServerChannelControl prepareProviderCommunicationChannel(EventProcessor provider, VodDatabase vod, VodEventClient client) {

		return factory.prepareCommunicationChannel(provider, vod, client);
	}
	

}
