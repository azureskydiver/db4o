package com.db4o.drs.versant.ipc.tcp;

import java.io.*;
import java.net.*;
import java.util.*;

import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.CommunicationChannelControl;
import com.db4o.drs.versant.ipc.*;
import com.db4o.internal.*;
import com.db4o.rmi.*;

public class TcpServer implements CommunicationChannelControl {

	private volatile ServerSocket server;
	private Set<Dispatcher> dispatchers = new HashSet<Dispatcher>();

	private final ObjectLifecycleMonitor provider;

	private Thread serverThread;

	public TcpServer(ObjectLifecycleMonitor provider) {

		this.provider = provider;

		serverThread = new Thread(ReflectPlatform.simpleName(ObjectLifecycleMonitor.class) + " channel tcp server") {
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
		server = null;
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
			server = new ServerSocket(TcpCommunicationNetwork.PORT, 100);
			server.setReuseAddress(true);
			notifyAll();
		}

		while (true) {
			Socket socket = server.accept();
			synchronized (dispatchers) {
				if (server == null) {
					break;
				}
				dispatchers.add(new Dispatcher(socket));
			}
		}

	}

	private void runServer() {
		try {
			runServer0();
		} catch (IOException e) {
		} finally {
			synchronized (this) {
				notifyAll();
			}
		}
	}

	public class Dispatcher implements Runnable {

		private final Socket client;
		private Thread thread;

		public Dispatcher(Socket socket) {
			this.client = socket;
			thread = new Thread(this, ReflectPlatform.simpleName(ObjectLifecycleMonitor.class)+" dispatcher for socket: " + socket);
			thread.setDaemon(true);
			thread.start();
		}

		public void close() throws IOException {
			client.close();
			try {
				thread.join();
			} catch (InterruptedException e) {
			}
		}

		public void run() {
			try {
				DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
				final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

				ByteArrayConsumer outgoingConsumer = new ByteArrayConsumer() {

					public void consume(byte[] buffer, int offset, int length) throws IOException {
						out.writeInt(length);
						out.write(buffer, offset, length);
						out.flush();
					}
				};
				Distributor<ObjectLifecycleMonitor> localPeer = new Distributor<ObjectLifecycleMonitor>(outgoingConsumer, provider);
				while (true) {
					TcpCommunicationNetwork.feed(in, localPeer);
				}
			} catch (IOException e) {
			} finally {
				synchronized (dispatchers) {
					dispatchers.remove(this);
				}
			}
		}

	}

}
