package com.db4o.drs.versant.ipc.tcp;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.net.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;

public class TcpCommunicationNetwork implements ObjectLifecycleMonitorNetwork {

	static final String HOST = "localhost";
	static final int PORT = 7283;

	public ObjectLifecycleMonitor newClient(final VodCobraFacade cobra, final int senderId) {
		
		// lazy initialization required because the client is created before ObjectLifecycleMonitor is up
		return (ObjectLifecycleMonitor) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ObjectLifecycleMonitor.class}, new InvocationHandler() {
			
			ObjectLifecycleMonitor forward = null;
			
			public Object invoke(Object arg0, Method arg1, Object[] arg2)
					throws Throwable {

				if (forward == null) {
					forward = newClient0(cobra, senderId);
				}
				
				return arg1.invoke(forward, arg2);
			}
		});

	}
	
	private ObjectLifecycleMonitor newClient0(final VodCobraFacade cobra, final int senderId) {
		
		
		try {
			Socket s = connect();

			final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
			final DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

			final Distributor<ObjectLifecycleMonitor> remotePeer = new Distributor<ObjectLifecycleMonitor>(new ByteArrayConsumer() {

				public void consume(byte[] buffer, int offset, int length) throws IOException {
					out.writeInt(length);
					out.write(buffer, offset, length);
					out.flush();
				}
			}, ObjectLifecycleMonitor.class);

			remotePeer.setFeeder(new Runnable() {

				public void run() {
					try {

						feed(in, remotePeer);
					} catch (IOException e) {
					}
				}
			});

			return remotePeer.sync();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	static void feed(final DataInputStream in, final ByteArrayConsumer consumer) throws IOException {
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

	private Socket connect() throws IOException {
		Socket s;
		while (true) {
			try {
				s = new Socket(HOST, PORT);
				break;
			} catch (ConnectException e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					throw new RuntimeException(e1);
				}
			} catch (UnknownHostException e) {
				throw new RuntimeException(e);
			}
		}
		return s;
	}

	public CommunicationChannelControl prepareCommunicationChannel(final ObjectLifecycleMonitor provider, final Object lock, final VodCobraFacade cobra, VodEventClient client,
			int senderId) {
		
		return new TcpServer(provider);
		
	}



}
