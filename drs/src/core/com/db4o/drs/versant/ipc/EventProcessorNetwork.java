package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;

public interface EventProcessorNetwork {

	ProviderSideCommunication newClient(final VodCobra cobra, final int senderId);

	Thread prepareProviderCommunicationChannel(ProviderSideCommunication provider, final Object lock, final VodCobra cobra, VodEventClient client, int senderId);

}
