package com.db4o.drs.versant.ipc.tcp;

import java.io.*;
import java.lang.reflect.*;
import java.lang.reflect.Proxy;
import java.net.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;

public class TcpCommunicationNetwork implements EventProcessorNetwork {

	static final String EVENT_PROCESSOR_HOST = "localhost";
	static final int EVENT_PROCESSOR_PORT = 7283;

	public ProviderSideCommunication newClient(final VodCobra cobra, final int senderId) {
		
		// lazy initialization required because the client is created before EventProcessor is up
		return (ProviderSideCommunication) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{ProviderSideCommunication.class}, new InvocationHandler() {
			
			ProviderSideCommunication forward = null;
			
			public Object invoke(Object arg0, Method arg1, Object[] arg2)
					throws Throwable {

				if (forward == null) {
					forward = newClient0(cobra, senderId);
				}
				
				return arg1.invoke(forward, arg2);
			}
		});

	}
	
	private ProviderSideCommunication newClient0(final VodCobra cobra, final int senderId) {
		
		
		try {
			Socket s = connect();

			final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
			final DataInputStream in = new DataInputStream(new BufferedInputStream(s.getInputStream()));

			final SimplePeer<ProviderSideCommunication> remotePeer = new SimplePeer<ProviderSideCommunication>(new ByteArrayConsumer() {

				public void consume(byte[] buffer, int offset, int length) throws IOException {
					out.writeInt(length);
					out.write(buffer, offset, length);
					out.flush();
				}
			}, ProviderSideCommunication.class);

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
				s = new Socket(EVENT_PROCESSOR_HOST, EVENT_PROCESSOR_PORT);
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

	public CommunicationChannelControl prepareProviderCommunicationChannel(final ProviderSideCommunication provider, final Object lock, final VodCobra cobra, VodEventClient client,
			int senderId) {
		
		return new Server(provider);
		
	}



}
