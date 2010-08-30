package com.db4o.rmi.test;

import java.io.*;
import java.util.*;

import com.db4o.foundation.*;
import com.db4o.rmi.*;

public class Feeder extends TheSimplest {

	private Thread clientFeederThread;

	public static class AccConsumer implements ByteArrayConsumer {

		private BlockingQueue4<byte[]> q = new BlockingQueue<byte[]>();

		public void consume(byte[] buffer, int offset, int length) throws IOException {

			q.add(Arrays.copyOfRange(buffer, offset, offset + length));
		}

		public byte[] take() throws InterruptedException {
			return q.hasNext() ? q.next() : null;
		}

	}

	public void setUp() {

		final AccConsumer serverProducedBuffers = new AccConsumer();

		concreteFacade = new FacadeImpl();

		server = new Distributor<Facade>(serverProducedBuffers, concreteFacade);
		client = new Distributor<Facade>(server, Facade.class);

		final Distributor c = (Distributor) client;
		c.setFeeder(new Runnable() {

			public void run() {

				try {
					byte[] buffer = serverProducedBuffers.take();

					if (buffer == null) {
						return;
					}
					
					client.consume(buffer, 0, buffer.length);

				} catch (InterruptedException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

		clientFeederThread = new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						Thread.sleep(1);
						c.getFeeder().run();
					}
				} catch (InterruptedException e) {
				}
			}
		};
		clientFeederThread.setDaemon(true);
		clientFeederThread.start();

	}

	@Override
	public void tearDown() throws Exception {
		clientFeederThread.interrupt();
		try {
			clientFeederThread.join();
		} catch (InterruptedException e) {
		}
		super.tearDown();
	}

}
