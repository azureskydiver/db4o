package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;

public interface EventProcessorNetwork {

	ProviderSideCommunication newClient(final VodCobra cobra, final int senderId);

	CommunicationChannelControl prepareProviderCommunicationChannel(ProviderSideCommunication provider, final Object lock, final VodCobra cobra, VodEventClient client, int senderId);
	
	public interface CommunicationChannelControl {
		void stop();
		void join() throws InterruptedException;
		void start();
	}

}
