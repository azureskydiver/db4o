package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.ClientChannelControl;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.ServerChannelControl;
import com.db4o.drs.versant.ipc.inband.*;
import com.db4o.drs.versant.ipc.tcp.*;

public class ObjectLifecycleMonitorNetworkFactory {
	
	public static final boolean USE_IN_BAND_COMMUNICATION = false;
	
	private static final ObjectLifecycleMonitorNetwork factory = USE_IN_BAND_COMMUNICATION ? new InBandCommunicationNetwork() : new TcpCommunicationNetwork();

	public static ClientChannelControl newClient(VodDatabase vod) {

		return factory.newClient(vod);
	}

	public static ServerChannelControl prepareProviderCommunicationChannel(ObjectLifecycleMonitor provider, VodDatabase vod, VodEventClient client) {

		return factory.prepareCommunicationChannel(provider, vod, client);
	}
	

}
