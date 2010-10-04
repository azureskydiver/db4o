package com.db4o.drs.versant.ipc;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;

public interface EventProcessorNetwork {

	ClientChannelControl newClient(VodDatabase vod);
	

	ServerChannelControl prepareCommunicationChannel(EventProcessor eventProcessor, VodDatabase vod, VodEventClient client);
	
	public interface ServerChannelControl {
		void stop();
		void join() throws InterruptedException;
	}
	
	public interface ClientChannelControl {
		EventProcessor sync();
		EventProcessor async();
		void stop();
		void join() throws InterruptedException;
	}

}
