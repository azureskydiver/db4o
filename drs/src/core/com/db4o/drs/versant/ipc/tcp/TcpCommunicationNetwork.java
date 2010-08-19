package com.db4o.drs.versant.ipc.tcp;

import java.io.*;
import java.net.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;

public class TcpCommunicationNetwork implements EventProcessorNetwork {

	private static final String EVENT_PROCESSOR_HOST = "localhost";
	private static final int EVENT_PROCESSOR_PORT = 7283;

	public ProviderSideCommunication newClient(final VodCobra cobra, final int senderId) {
		

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
						e.printStackTrace();
						// throw new RuntimeException(e);
					}
				}
			});

			return remotePeer.sync();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void feed(final DataInputStream in, final ByteArrayConsumer consumer) throws IOException {
		int len = in.readInt();
		int read = 0;
		byte[] buffer = new byte[len];
		while (read < len) {
			in.read(buffer, read, len - read);
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
//				e.printStackTrace();
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

	public Thread prepareProviderCommunicationChannel(final ProviderSideCommunication provider, final Object lock, final VodCobra cobra, VodEventClient client,
			int senderId) {

		Thread t = new Thread("eventprocessor channel") {
			@Override
			public void run() {

				try {

					serverServer(provider);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		t.setDaemon(true);

		return t;
	}

	private void serverServer(final ProviderSideCommunication provider) throws IOException, SocketException {
		ServerSocket server = new ServerSocket(EVENT_PROCESSOR_PORT, 100);
		server.setReuseAddress(true);

		Socket client = server.accept();

		DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
		final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

		ByteArrayConsumer outgoingConsumer = new ByteArrayConsumer() {

			public void consume(byte[] buffer, int offset, int length) throws IOException {
				out.writeInt(length);
				out.write(buffer, offset, length);
				out.flush();
			}
		};
		SimplePeer<ProviderSideCommunication> localPeer = new SimplePeer<ProviderSideCommunication>(outgoingConsumer, provider);
		while (true) {
			feed(in, localPeer);
		}
	}


}
