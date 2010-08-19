package com.db4o.rmi.test;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.db4o.rmi.*;

public class Decoupled extends TheSimplest {

	public static class DecoupledConsumer implements ByteArrayConsumer {

		private BlockingQueue<byte[]> q = new LinkedBlockingQueue<byte[]>();

		public DecoupledConsumer(final ByteArrayConsumer consumer) {
			Thread t = new Thread("Decoupled Consumer for " + consumer) {
				@Override
				public void run() {
					try {
						while (true) {
							byte[] buffer = q.take();
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
			q.offer(Arrays.copyOfRange(buffer, offset, offset+length));
		}

	}
	
	public void setUp() {
		super.setUp();
		server.setConsumer(new DecoupledConsumer(client));
		client.setConsumer(new DecoupledConsumer(server));
	}

}
