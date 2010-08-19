package com.db4o.rmi.test;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import com.db4o.rmi.*;

public class Feeder extends TheSimplest {
	
	public static class AccConsumer implements ByteArrayConsumer {

		private BlockingQueue<byte[]> q = new LinkedBlockingQueue<byte[]>();
		
		public void consume(byte[] buffer, int offset, int length) throws IOException {

			q.offer(Arrays.copyOfRange(buffer, offset, offset+length));
		}
		
		public byte[] take() throws InterruptedException {
			return q.take();
		}
		
	}


	public void setUp() {
		
		final AccConsumer serverProducedBuffers = new AccConsumer();

		server = new SimplePeer<Facade>(serverProducedBuffers, new FacadeImpl());
		client = new SimplePeer<Facade>(server, Facade.class);

		((SimplePeer<Facade>) client).setFeeder(new Runnable() {
			
			public void run() {

				try {
					byte[] buffer = serverProducedBuffers.take();
					
					client.consume(buffer, 0, buffer.length);
					
				} catch (InterruptedException e) {
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}

}
