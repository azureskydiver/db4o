package com.db4o.rmi.test;

import java.io.*;
import java.util.*;

import com.db4o.foundation.*;
import com.db4o.rmi.*;

public class Decoupled extends TheSimplest {

	private DecoupledConsumer serverConsumer;
	private DecoupledConsumer clientConsumer;

	public static class DecoupledConsumer implements ByteArrayConsumer {

		private BlockingQueue4<byte[]> q = new BlockingQueue<byte[]>();
		private Thread t;

		public DecoupledConsumer(final ByteArrayConsumer consumer) {
			t = new Thread("Decoupled Consumer for " + consumer) {
				@Override
				public void run() {
					try {
						while (true) {
							byte[] buffer = q.next();
							consumer.consume(buffer, 0, buffer.length);
						}
					} catch (Exception e) {
					}
				}
			};
			t.setDaemon(true);
			t.start();
		}

		public void consume(byte[] buffer, int offset, int length) throws IOException {
			q.add(Arrays.copyOfRange(buffer, offset, offset+length));
		}

		public void dispose() {
			q.stop();
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}

	}
	
	@Override
	public void setUp() {
		super.setUp();
		serverConsumer = new DecoupledConsumer(client);
		clientConsumer = new DecoupledConsumer(server);
		server.setConsumer(serverConsumer);
		client.setConsumer(clientConsumer);
	}
	
	@Override
	public void tearDown() throws Exception {
		serverConsumer.dispose();
		clientConsumer.dispose();
		super.tearDown();
	}

}
