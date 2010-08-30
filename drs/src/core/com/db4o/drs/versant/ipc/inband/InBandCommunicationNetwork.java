package com.db4o.drs.versant.ipc.inband;

import java.io.*;
import java.util.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;

public class InBandCommunicationNetwork implements ObjectLifecycleMonitorNetwork {
	
	public abstract static class Ticker implements Runnable {

		private Thread thread;
		private volatile boolean running = true;
		private final long sleepInMillis;

		public Ticker(String name, long sleepInMillis) {
			this.sleepInMillis = sleepInMillis;
			thread = new Thread(this, name);
			thread.setDaemon(true);
		}

		public abstract boolean tick();

		public void run() {
			while(running) {
				snooze();
				if (!running) {
					break;
				}
				if (!tick()) {
					break;
				}
			}
		}

		private synchronized void snooze() {
			try {
				wait(sleepInMillis);
			} catch (InterruptedException e) {
				running = false;
			}
		}

		public void start() {
			thread.start();
		}

		public void stop() {
			running = false;
		}

		public void join() throws InterruptedException {
			thread.join();
		}
		
	}

	public ClientChannelControl newClient(final VodCobraFacade cobra, final int senderId) {
		
		final Distributor<ObjectLifecycleMonitor> remotePeer = new Distributor<ObjectLifecycleMonitor>(new ByteArrayConsumer() {

			public void consume(byte[] buffer, int offset, int length) throws IOException {
				MessagePayload msg = new MessagePayload(senderId, Arrays.copyOfRange(buffer, offset, offset + length));
				cobra.store(msg);
				cobra.commit();

			}
		}, ObjectLifecycleMonitor.class);
		
		
		final Object feederLock = new Object();
		
		final Ticker feeder = new Ticker("In band client feeder", 1000) {
			@Override
			public boolean tick() {
				synchronized (feederLock) {
					try {
						feed(cobra, senderId, remotePeer, true);
					} catch (IOException e) {
						return false;
					}
				}
				return true;
			}
		};
		feeder.start();
		

		remotePeer.setFeeder(new Runnable() {

			public void run() {
				try {
					synchronized (feederLock) {
						feed(cobra, senderId, remotePeer, false);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		
		return new ClientChannelControl() {
			
			public ObjectLifecycleMonitor sync() {
				return remotePeer.sync();
			}
			
			public void stop() {
				feeder.stop();
			}

			public void join() throws InterruptedException {
				feeder.join();
			}

			public ObjectLifecycleMonitor async() {
				return remotePeer.async();
			}
		};


		
	}

	private static void feed(VodCobraFacade _cobra, int senderId, ByteArrayConsumer consumer, boolean runJustOnce) throws IOException {
		boolean atLeastOne = false;
		while (!atLeastOne) {
			Collection<MessagePayload> msgs = _cobra.query(MessagePayload.class);
			for (MessagePayload msg : msgs) {
				if (msg.sender() == senderId || msg.consumed()) {
					continue;
				}
				atLeastOne = true;
				consumer.consume(msg.buffer(), 0, msg.buffer().length);
				msg.consumedAt(System.currentTimeMillis());
				_cobra.store(msg);
				_cobra.commit();
			}
			if (runJustOnce) {
				break;
			}
			if (!atLeastOne) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public ServerChannelControl prepareCommunicationChannel(ObjectLifecycleMonitor provider, Object lock, VodCobraFacade cobra,
			VodEventClient client, int senderId) {

		return new InBandServer(provider, lock, cobra, client, senderId);

	}
}
