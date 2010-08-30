package com.db4o.drs.versant.ipc.inband;

import java.io.*;
import java.util.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.rmi.*;

public class InBandCommunicationNetwork implements ObjectLifecycleMonitorNetwork {

	public ObjectLifecycleMonitor newClient(final VodCobra cobra, final int senderId) {

		final Distributor<ObjectLifecycleMonitor> remotePeer = new Distributor<ObjectLifecycleMonitor>(new ByteArrayConsumer() {

			public void consume(byte[] buffer, int offset, int length) throws IOException {
				MessagePayload msg = new MessagePayload(senderId, Arrays.copyOfRange(buffer, offset, offset + length));
				cobra.store(msg);
				cobra.commit();

			}
		}, ObjectLifecycleMonitor.class);

		remotePeer.setFeeder(new Runnable() {

			public void run() {
				try {
					feed(cobra, senderId, remotePeer);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});

		return remotePeer.sync(); // TODO: should return the peer itself to let the client choose between sync and async

	}

	private static void feed(VodCobra _cobra, int senderId, ByteArrayConsumer consumer) throws IOException {
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
			if (!atLeastOne) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public CommunicationChannelControl prepareCommunicationChannel(ObjectLifecycleMonitor provider, Object lock, VodCobra cobra,
			VodEventClient client, int senderId) {

		return new InBandServer(provider, lock, cobra, client, senderId);

	}
}
