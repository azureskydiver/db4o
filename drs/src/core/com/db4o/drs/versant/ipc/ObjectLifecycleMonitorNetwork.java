package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;

public interface ObjectLifecycleMonitorNetwork {

	ClientChannelControl newClient(final VodCobraFacade cobra, final int senderId);
	

	ServerChannelControl prepareCommunicationChannel(ObjectLifecycleMonitor monitor, final Object lock, final VodCobraFacade cobra, VodEventClient client, int senderId);
	
	public interface ServerChannelControl {
		void stop();
		void join() throws InterruptedException;
	}
	
	public interface ClientChannelControl {
		ObjectLifecycleMonitor sync();
		ObjectLifecycleMonitor async();
		void stop();
		void join() throws InterruptedException;
	}

}
