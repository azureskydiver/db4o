package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;

public interface ObjectLifecycleMonitorNetwork {

	ClientChannelControl newClient(VodDatabase vod);
	

	ServerChannelControl prepareCommunicationChannel(ObjectLifecycleMonitor monitor, VodDatabase vod, VodEventClient client);
	
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
