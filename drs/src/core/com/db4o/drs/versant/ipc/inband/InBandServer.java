package com.db4o.drs.versant.ipc.inband;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.CommunicationChannelControl;
import com.db4o.drs.versant.ipc.*;
import com.db4o.foundation.*;
import com.db4o.qlin.*;
import com.db4o.rmi.*;
import com.versant.event.*;

public class InBandServer implements CommunicationChannelControl {

	private final ObjectLifecycleMonitor provider;
	private final Object lock;
	private final VodCobra cobra;
	private final VodEventClient client;
	private final int senderId;
	private BlockingQueue4<MessagePayload> pendingMessages;
	private ByteArrayConsumer outgoingConsumer;
	private Thread serverThread;
	private Distributor<ObjectLifecycleMonitor> localPeer;
	private SimpleTimer purger;
	private Thread purgerThread;

	public InBandServer(ObjectLifecycleMonitor provider, Object lock, VodCobra cobra, VodEventClient client, int senderId) {
		this.provider = provider;
		this.lock = lock;
		this.cobra = cobra;
		this.client = client;
		this.senderId = senderId;

		startMessagePayloadPurger();

		outgoingConsumer = prepareConsumerForOutgoingMessages();

		localPeer = new Distributor<ObjectLifecycleMonitor>(this.outgoingConsumer, this.provider);

		pendingMessages = new BlockingQueue<MessagePayload>();

		prepareChannelForIncomingMessages();

		serverThread = new Thread("In band communication server") {
			@Override
			public void run() {
				taskQueueProcessorLoop();
			}
		};
		serverThread.setDaemon(true);

	}

	public void stop() {
		pendingMessages.stop();
		purger.stop();
	}

	public void start() {
		serverThread.start();
	}

	public void join() throws InterruptedException {
		serverThread.join();
		purgerThread.join();
	}

	private void startMessagePayloadPurger() {
		purger = new SimpleTimer(new Runnable() {
			public void run() {
				synchronized (lock) {
					MessagePayload prototype = QLinSupport.prototype(MessagePayload.class);
					ObjectSet<MessagePayload> q = cobra.from(MessagePayload.class).where(prototype.consumedAt()).smaller(System.currentTimeMillis() - 5000).select();
					if (q.isEmpty()) {
						return;
					}
					while (q.hasNext()) {
						MessagePayload payload = q.next();
						if (!payload.consumed()) {
							continue;
						}
						cobra.delete(payload.loid());
					}
					cobra.commit();
				}
				;
			}
		}, 2000);

		purgerThread = new Thread(purger, "In-band message payload purger");
		purgerThread.setDaemon(true);
		purgerThread.start();
	}

	private void taskQueueProcessorLoop() {

		MessagePayload msg;

		try {
			while ((msg = pendingMessages.next()) != null) {
				synchronized (lock) {
					msg.consumedAt(System.currentTimeMillis());
					cobra.store(msg);
					cobra.commit();
					byte[] buffer = msg.buffer();
					try {
						localPeer.consume(buffer, 0, buffer.length);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch (BlockingQueueStoppedException e) {
		}
	}

	private ByteArrayConsumer prepareConsumerForOutgoingMessages() {

		return new ByteArrayConsumer() {

			public void consume(byte[] buffer, int offset, int length) throws IOException {

				synchronized (lock) {
					byte[] copy = Arrays.copyOfRange(buffer, offset, length + offset);
					MessagePayload msg = new MessagePayload(senderId, copy);
					cobra.store(msg);
					cobra.commit();
				}
			}
		};
	}

	private void prepareChannelForIncomingMessages() {

		EventChannel channel = client.produceClassChannel(MessagePayload.class.getName());

		channel.addVersantEventListener(new ClassEventListener() {
			public void instanceCreated(VersantEventObject event) {
				final String raiserLoid = event.getRaiserLoid();
				long messageLoid = VodCobra.loidAsLong(raiserLoid);
				final MessagePayload msg;
				synchronized (lock) {
					msg = cobra.objectByLoid(messageLoid);
				}
				if (msg.sender() == senderId || msg.consumed()) {
					return;
				}
				pendingMessages.add(msg);
			}

			public void instanceModified(VersantEventObject event) {
			}

			public void instanceDeleted(VersantEventObject event) {
			}
		});
	}

}
