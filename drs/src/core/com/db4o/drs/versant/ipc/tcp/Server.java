package com.db4o.drs.versant.ipc.tcp;

import java.io.*;
import java.net.*;
import java.util.*;

import com.db4o.drs.versant.ipc.EventProcessorNetwork.CommunicationChannelControl;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;

public class Server implements CommunicationChannelControl {

	private ServerSocket server;
	private Set<Dispatcher> dispatchers = new HashSet<Dispatcher>();

	private final ProviderSideCommunication provider;

	private Thread serverThread;

	public Server(ProviderSideCommunication provider) {

		this.provider = provider;

		serverThread = new Thread("EventProcessor channel tcp server") {
			@Override
			public void run() {
				runServer();
			}
		};
		serverThread.setDaemon(true);

	}

	public void stop() {
		stopServer();
		stopDispatchers();
	}

	private void stopServer() {
		ServerSocket s = server;
		if (s != null) {
			try {
				s.close();
			} catch (IOException e) {
			}
		}
	}

	private void stopDispatchers() {
		List<Dispatcher> cs = new ArrayList<Dispatcher>();
		synchronized (dispatchers) {
			cs.addAll(dispatchers);
		}
		for (Dispatcher socket : cs) {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
	}

	public void start() {
		serverThread.start();
		waitForServerReady();
	}

	private synchronized void waitForServerReady() {
		if (server == null) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	public void join() throws InterruptedException {
		serverThread.join();
	}

	private void runServer0() throws IOException, SocketException {
		synchronized (this) {
			server = new ServerSocket(TcpCommunicationNetwork.EVENT_PROCESSOR_PORT, 100);
			notifyAll();
		}
		server.setReuseAddress(true);

		while (true) {
			new Dispatcher(server.accept());
		}

	}

	private void runServer() {
		try {
			runServer0();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			synchronized (this) {
				notifyAll();
			}
		}
	}

	public class Dispatcher implements Runnable {

		private final Socket client;

		public Dispatcher(Socket socket) {
			this.client = socket;
			Thread t = new Thread(this, "EventProcessor dispatcher for socket: " + socket);
			t.setDaemon(true);
			t.start();
		}

		public void close() throws IOException {
			synchronized (dispatchers) {
				dispatchers.remove(this);
			}
			client.close();
		}

		public void run() {
			try {
				System.out.println("dispatcher up: "  + client);
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
					TcpCommunicationNetwork.feed(in, localPeer);
				}
			} catch (IOException e) {
			} finally {
				System.out.println("dispatcher died: " + client);
				synchronized (dispatchers) {
					dispatchers.remove(this);
				}
			}
		}

	}

}
