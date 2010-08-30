package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;

public interface ObjectLifecycleMonitorNetwork {

	ObjectLifecycleMonitor newClient(final VodCobra cobra, final int senderId);

	CommunicationChannelControl prepareCommunicationChannel(ObjectLifecycleMonitor monitor, final Object lock, final VodCobra cobra, VodEventClient client, int senderId);
	
	public interface CommunicationChannelControl {
		void start();
		void stop();
		void join() throws InterruptedException;
	}

}
