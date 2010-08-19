package com.db4o.rmi.test;

import java.util.concurrent.*;

import com.db4o.rmi.*;

import db4ounit.*;

public class TheSimplest implements TestCase, TestLifeCycle {

	public static interface Facade {
		int intCall();

		void voidCall();
	}

	public static class FacadeImpl implements Facade {

		public int intCall() {
			return 42;
		}

		public void voidCall() {
		}
	}

	Peer<Facade> client;
	Peer<Facade> server;
	
	public void setUp() {

		server = new SimplePeer<Facade>(null, new FacadeImpl());
		client = new SimplePeer<Facade>(null, Facade.class);

		server.setConsumer(client);
		client.setConsumer(server);
	}

	public void testBasic() throws InterruptedException {

		Assert.areEqual(42, client.sync().intCall());
		
		final BlockingQueue<Integer> q = new LinkedBlockingQueue<Integer>();
		
		client.async(new Callback<Integer>() {
		
			public void returned(Integer value) {
				q.add(value);
			}
		}).intCall();
		
		Assert.areEqual(42, (int) q.take());
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
		}
		
		Assert.isTrue(q.isEmpty());
		
		client.sync().voidCall();

	}

	public void tearDown() throws Exception {
	}

}
