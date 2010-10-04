package com.db4o.drs.versant.ipc.tcp;

import java.io.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;

public class TcpCommunicationNetwork implements EventProcessorNetwork {

	static final String HOST = "localhost";
	static final int PORT = 7283;

	public ClientChannelControl newClient(VodDatabase vod) {
		
		return new TcpClient();
	}

	public ServerChannelControl prepareCommunicationChannel(final EventProcessor provider, VodDatabase vod,
			VodEventClient client) {

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
