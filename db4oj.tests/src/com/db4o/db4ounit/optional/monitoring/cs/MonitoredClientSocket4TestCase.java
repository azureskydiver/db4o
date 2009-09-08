/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;


import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;

import db4ounit.Assert;

@decaf.Ignore
public class MonitoredClientSocket4TestCase extends MonitoredSocket4TestCaseBase {
	
	public void configureClient(Configuration config) throws Exception {
		configure(config);		
	}

	public void configureServer(Configuration config) throws Exception {
	}
	
	@Override
	protected void configure(Configuration legacy) throws Exception {
		super.configure(legacy);
		_socket4Factory = setupNewSocketFactory(legacy);
	}
	
	public void testBytesSent() {
		storeAndAdvanceClock();
		
		Assert.isGreater(0, (long) observedBytesSent(db()));
		Assert.areEqual(observedBytesSent(db()), bean().<Double>getAttribute("BytesSentPerSecond").doubleValue());
	}

	public void testBytesSentTwoClients() {
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			assertBytesSent(client1, new Item("bar"));
			assertBytesSent(client2, new Item("foobar"));			
		}});		
	}
	
	public void testBytesSentTwoClientsInterleaved() {
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			double clientCount1 = storeAndReturnObservedBytesSent(client1, new Item("bar"));			
			double clientCount2 = storeAndReturnObservedBytesSent(client2, new Item("foobar"));
			_clock.advance(1000);
			
			Assert.areEqual(clientCount1, getBytesSentPerSecond(client1), "Client 1");
			Assert.areEqual(clientCount2, getBytesSentPerSecond(client2), "Client 2");					
		}});
	}

	@Override
	protected void withTwoClients(final TwoClientsAction action) {
		super.withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			resetBeanCounterFor(client1, client2);
			action.apply(client1, client2);			
		}});
	}

	private void assertBytesSent(ObjectContainer client, Item item) {
		resetBeanCounterFor(client); 
		
		double expectedCount = storeAndReturnObservedBytesSent(client, item);
		_clock.advance(1000);
		
		Assert.areEqual(expectedCount, getBytesSentPerSecond(client));
	}
	
	private void storeAndAdvanceClock() {
		store(new Item("foo"));
		_clock.advance(1000);
	}

	private double storeAndReturnObservedBytesSent(ObjectContainer client, Item item) {		
		resetCountingSocket(client);
		client.store(item);
		return observedBytesSent(client);
	}

	private void resetCountingSocket(ObjectContainer container) {
		CountingSocket4Factory countingSocketFactory = configuredSocketFactoryFor(container);

		for(CountingSocket4 socket : countingSocketFactory.countingSockets()) {
			socket.resetCount();
		}
	}
}