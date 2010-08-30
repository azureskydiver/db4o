package com.db4o.drs.versant.ipc.tcp;

import java.io.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;

public class TcpCommunicationNetwork implements ObjectLifecycleMonitorNetwork {

	static final String HOST = "localhost";
	static final int PORT = 7283;

	public ClientChannelControl newClient(final VodCobraFacade cobra, final int senderId) {
		
		return new TcpClient();
	}

	public ServerChannelControl prepareCommunicationChannel(final ObjectLifecycleMonitor provider, final Object lock, final VodCobraFacade cobra,
			VodEventClient client, int senderId) {

		return new TcpServer(provider);
	}
	
	

	public static void feed(final DataInputStream in, final ByteArrayConsumer consumer) throws IOException {
		int len = in.readInt();
		int read = 0;
		byte[] buffer = new byte[len];
		while (read < len) {
			int ret = in.read(buffer, read, len - read);
			if (ret == -1) {
				throw new EOFException();
			}
			read += ret;
		}

		consumer.consume(buffer, 0, len);
	}

}
