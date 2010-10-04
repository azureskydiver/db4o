package com.db4o.drs.versant.ipc.inband;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;
import com.db4o.util.*;

public class InBandCommunicationNetwork implements EventProcessorNetwork {
	
	
	private static AtomicInteger nextSenderId = new AtomicInteger();
	
	public ClientChannelControl newClient(VodDatabase vod) {
		
		final int senderId = nextSenderId.getAndIncrement();
		
		final VodCobraFacade lcobra = VodCobra.createInstance(vod);
		
		InBandServer.purgeMessagePayloads(lcobra);
		
		final Distributor<EventProcessor> remotePeer = new Distributor<EventProcessor>(new ByteArrayConsumer() {

			public void consume(byte[] buffer, int offset, int length) throws IOException {
				MessagePayload msg = new MessagePayload(senderId, ArrayUtil.copy(buffer, offset, offset + length));
				synchronized (lcobra) {
					lcobra.store(msg);
					lcobra.commit();
				}

			}
		}, EventProcessor.class);
		
		
		final Object feederLock = new Object();
		
		final Ticker feeder = new Ticker("In band client feeder", 100) {
			@Override
			public boolean tick() {
				synchronized (feederLock) {
					try {
						synchronized (lcobra) {
							if (!isRunning()) {
								return false;
							}
							feed(lcobra, senderId, remotePeer, true);
						}
					} catch (IOException e) {
						return false;
					}
				}
				return true;
			}
		};
		feeder.start();
		
		return new ClientChannelControl() {
			
			public EventProcessor sync() {
				return remotePeer.sync();
			}
			
			public void stop() {
				feeder.stop();
				synchronized (lcobra) {
					lcobra.close();
				}
			}

			public void join() throws InterruptedException {
				feeder.join();
			}

			public EventProcessor async() {
				return remotePeer.async();
			}
		};


		
	}

	private static void feed(VodCobraFacade cobra, int senderId, ByteArrayConsumer consumer, boolean runJustOnce) throws IOException {
		boolean atLeastOne = false;
		while (!atLeastOne) {
			Collection<MessagePayload> msgs = cobra.query(MessagePayload.class);
			for (MessagePayload msg : msgs) {
				if (msg.sender() == senderId || msg.consumed()) {
					continue;
				}
				atLeastOne = true;
				msg.consumedAt(System.currentTimeMillis());
				cobra.store(msg);
				cobra.commit();
				consumer.consume(msg.buffer(), 0, msg.buffer().length);
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

	public ServerChannelControl prepareCommunicationChannel(EventProcessor provider, VodDatabase vod,
			VodEventClient client) {

		int senderId = nextSenderId.getAndIncrement();
		
		return new InBandServer(provider, new Object(), VodCobra.createInstance(vod), client, senderId);

	}
}
