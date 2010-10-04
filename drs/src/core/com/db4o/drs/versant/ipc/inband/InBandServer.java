package com.db4o.drs.versant.ipc.inband;

import java.io.*;
import java.util.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.EventProcessorNetwork.ServerChannelControl;
import com.db4o.foundation.*;
import com.db4o.rmi.*;
import com.db4o.util.*;
import com.versant.event.*;

public class InBandServer implements ServerChannelControl {

	private final EventProcessor provider;
	private final Object lock;
	private final VodCobraFacade cobra;
	private final VodEventClient client;
	private final int senderId;
	private BlockingQueue4<String> pendingMessages;
	private ByteArrayConsumer outgoingConsumer;
	private Thread serverThread;
	private Distributor<EventProcessor> localPeer;
	private Ticker purger;

	public InBandServer(EventProcessor provider, Object lock, VodCobraFacade cobra, VodEventClient client, int senderId) {
		this.provider = provider;
		this.lock = lock;
		this.cobra = cobra;
		this.client = client;
		this.senderId = senderId;

		startMessagePayloadPurger();

		outgoingConsumer = prepareConsumerForOutgoingMessages();

		localPeer = new Distributor<EventProcessor>(this.outgoingConsumer, this.provider);

		pendingMessages = new BlockingQueue<String>();

		prepareChannelForIncomingMessages();

		serverThread = new Thread("In band communication server") {
			@Override
			public void run() {
				taskQueueProcessorLoop();
			}
		};
		serverThread.setDaemon(true);
		serverThread.start();
	}

	public void stop() {
		pendingMessages.stop();
		purger.stop();
		cobra.close();
	}

	public void join() throws InterruptedException {
		serverThread.join();
		purger.join();
	}

	private void startMessagePayloadPurger() {
		
		purgeMessagePayloads(cobra);

		
		purger = new Ticker("In-band message payload purger", 1000) {
			
			@Override
			public boolean tick() {
				synchronized (lock) {
					if (!isRunning()) {
						return false;
					}
//					MessagePayload prototype = QLinSupport.prototype(MessagePayload.class);
//					ObjectSet<MessagePayload> q = cobra.from(MessagePayload.class).where(prototype.consumedAt()).smaller(expirationDate).select();
					final long limit = System.currentTimeMillis()-10000;
					purge(cobra, new Function4<MessagePayload, Boolean>() {

						public Boolean apply(MessagePayload payload) {
							
							return payload.consumed() && payload.consumedAt() < limit;
						}
					});
				}
				return true;
			}
		};
		purger.start();
	}

	public static void purgeMessagePayloads(VodCobraFacade cobra) {
		purge(cobra, new Function4<MessagePayload, Boolean>() {
			public Boolean apply(MessagePayload payload) {
				return true;
			}
		});
	}

	private void taskQueueProcessorLoop() {


		try {
			String raiserLoid;
			while ((raiserLoid = pendingMessages.next()) != null) {
				synchronized (lock) {
					
					long messageLoid = VodCobra.loidAsLong(raiserLoid);
					
					MessagePayload msg = cobra.objectByLoid(messageLoid);
					if (msg.sender() == senderId || msg.consumed()) {
						continue;
					}
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
					byte[] copy = ArrayUtil.copy(buffer, offset, length + offset);
					MessagePayload msg = new MessagePayload(senderId, copy);
					cobra.store(msg);
					cobra.commit();
				}
			}
		};
	}

	private void prepareChannelForIncomingMessages() {

		EventChannel channel = client.produceClassChannel(MessagePayload.class.getName(), false);

		channel.addVersantEventListener(new ClassEventListener() {
			public void instanceCreated(VersantEventObject event) {
				pendingMessages.add(event.getRaiserLoid());
			}

			public void instanceModified(VersantEventObject event) {
			}

			public void instanceDeleted(VersantEventObject event) {
			}
		});
	}

	public static void purge(VodCobraFacade cobra, Function4<MessagePayload,Boolean> aval) {
		Collection<MessagePayload> q = cobra.query(MessagePayload.class);
		if (q.isEmpty()) {
			return;
		}
		boolean atLeastOne = false;
		for(MessagePayload payload : q) {
			if (!aval.apply(payload)) {
				continue;
			}
			atLeastOne = true;
			cobra.delete(payload.loid());
		}
		if (atLeastOne) {
			cobra.commit();
		}
	}

}
