package com.db4o.drs.versant.ipc.tcp;

import java.io.*;
import java.net.*;
import java.util.*;

import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.CommunicationChannelControl;
import com.db4o.drs.versant.ipc.*;
import com.db4o.internal.*;
import com.db4o.rmi.*;

public class TcpServer implements CommunicationChannelControl {

	private ServerSocket server;
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
			server = new ServerSocket(TcpCommunicationNetwork.OBJECT_LIFECYCLE_MONITOR_PORT, 100);
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
//			e.printStackTrace();
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
			Thread t = new Thread(this, ReflectPlatform.simpleName(ObjectLifecycleMonitor.class)+" dispatcher for socket: " + socket);
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
				DataInputStream in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
				final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

				ByteArrayConsumer outgoingConsumer = new ByteArrayConsumer() {

					public void consume(byte[] buffer, int offset, int length) throws IOException {
						out.writeInt(length);
						out.write(buffer, offset, length);
						out.flush();
					}
				};
				SimplePeer<ObjectLifecycleMonitor> localPeer = new SimplePeer<ObjectLifecycleMonitor>(outgoingConsumer, provider);
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
