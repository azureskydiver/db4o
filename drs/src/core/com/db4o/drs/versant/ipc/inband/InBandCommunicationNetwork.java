package com.db4o.drs.versant.ipc.inband;

import java.io.*;
import java.util.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.foundation.*;
import com.db4o.rmi.*;
import com.versant.event.*;

public class InBandCommunicationNetwork implements EventProcessorNetwork {

	public ProviderSideCommunication newClient(final VodCobra cobra, final int senderId) {

		final SimplePeer<ProviderSideCommunication> remotePeer = new SimplePeer<ProviderSideCommunication>(new ByteArrayConsumer() {

			public void consume(byte[] buffer, int offset, int length) throws IOException {
				RMIMessage msg = new RMIMessage(senderId, Arrays.copyOfRange(buffer, offset, offset + length));
				cobra.store(msg);
				cobra.commit();

			}
		}, ProviderSideCommunication.class);
		
		remotePeer.setFeeder(new Runnable() {

			public void run() {
				try {
					feed(cobra, senderId, remotePeer);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		return remotePeer.sync();

	}

	private static void feed(VodCobra _cobra, int senderId, ByteArrayConsumer consumer) throws IOException {
		boolean atLeastOne = false;
		while (!atLeastOne) {
			Collection<RMIMessage> msgs = _cobra.query(RMIMessage.class);
			for (RMIMessage msg : msgs) {
				if (msg.sender() == senderId) {
					continue;
				}
				atLeastOne = true;
				consumer.consume(msg.buffer(), 0, msg.buffer().length);
				_cobra.delete(msg.loid());
				_cobra.commit();
			}
			if (!atLeastOne) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public CommunicationChannelControl prepareProviderCommunicationChannel(ProviderSideCommunication provider, final Object lock, final VodCobra cobra, VodEventClient client,
			int senderId) {

		ByteArrayConsumer outgoingConsumer = prepareConsumerForOutgoingMessages(lock, cobra, senderId);

		final SimplePeer<ProviderSideCommunication> localPeer = new SimplePeer<ProviderSideCommunication>(outgoingConsumer, provider);

		final BlockingQueue4<RMIMessage> pendingMessages = new BlockingQueue<RMIMessage>();

		prepareChannelForIncomingMessages(client, lock, cobra, pendingMessages, senderId);

		
		final Thread t = new Thread("eventprocessor channel") {
			@Override
			public void run() {
				taskQueueProcessorLoop(pendingMessages, lock, cobra, localPeer);
			}
		};
		t.setDaemon(true);
		
		return new CommunicationChannelControl() {
			
			public void stop() {
				pendingMessages.stop();
			}
			
			public void start() {
				t.start();
			}
			
			public void join() throws InterruptedException {
				t.join();
			}
		};
	}
	
	private static void taskQueueProcessorLoop(BlockingQueue4<RMIMessage> pendingMessages, Object lock, VodCobra _cobra, ByteArrayConsumer _incomingMessages) {
		
		RMIMessage msg;
		
		try {
			while((msg = pendingMessages.next())!=null) {
				synchronized (lock) {
					_cobra.delete(msg.loid());
					_cobra.commit();
					byte[] buffer = msg.buffer();
					try {
						_incomingMessages.consume(buffer, 0, buffer.length);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		} catch (BlockingQueueStoppedException e){
		}
	}


	private static ByteArrayConsumer prepareConsumerForOutgoingMessages(final Object lock, final VodCobra cobra, final int senderId) {

		return new ByteArrayConsumer() {

			public void consume(byte[] buffer, int offset, int length) throws IOException {

				synchronized (lock) {
					byte[] copy = Arrays.copyOfRange(buffer, offset, length + offset);
					RMIMessage msg = new RMIMessage(senderId, copy);
					cobra.store(msg);
					cobra.commit();
				}
			}
		};
	}

	private static void prepareChannelForIncomingMessages(VodEventClient client, final Object lock, final VodCobra cobra,
			final BlockingQueue4<RMIMessage> pendingMessages, final int senderId) {

		EventChannel channel = client.produceClassChannel(RMIMessage.class.getName());

		channel.addVersantEventListener(new ClassEventListener() {
			public void instanceCreated(VersantEventObject event) {
				final String raiserLoid = event.getRaiserLoid();
				long messageLoid = VodCobra.loidAsLong(raiserLoid);
				final RMIMessage msg;
				synchronized (lock) {
					msg = cobra.objectByLoid(messageLoid);
				}
				if (msg.sender() == senderId) {
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
