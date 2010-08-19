package com.db4o.drs.versant.ipc;

import java.io.*;
import java.util.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.inband.*;
import com.db4o.foundation.*;
import com.db4o.rmi.*;
import com.versant.event.*;

public class InBandCommunicationFactory {

	public static ProviderSideCommunication newClient(final VodCobra cobra, final int senderId) {

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

	public static ByteArrayConsumer prepareProviderCommunicationChannel(ProviderSideCommunication provider, Object lock, VodCobra cobra, VodEventClient client,
			BlockingQueue<RMIMessage> pendingMessages, int senderId) {

		ByteArrayConsumer outgoingConsumer = prepareConsumerForOutgoingMessages(lock, cobra, senderId);

		SimplePeer<ProviderSideCommunication> localPeer = new SimplePeer<ProviderSideCommunication>(outgoingConsumer, provider);

		prepareChannelForIncomingMessages(client, lock, cobra, pendingMessages, senderId);

		return localPeer;
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
			final BlockingQueue<RMIMessage> pendingMessages, final int senderId) {

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
