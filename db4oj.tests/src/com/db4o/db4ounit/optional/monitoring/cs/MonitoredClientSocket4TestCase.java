/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;

import com.db4o.*;
import com.db4o.config.*;

import db4ounit.*;

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
		legacy.clientServer().batchMessages(false);
		legacy.clientServer().prefetchIDCount(1);
		
		_socket4Factory = setupNewSocketFactory(legacy);		
	}
	
	public void testBytesReceived() {
		exerciseSingleClient(new BytesReceivedCounterHandler());
	}
	
	public void testBytesSent() {
		exerciseSingleClient(new BytesSentCounterHandler());
	}

	public void testMessagesSent() {
		exerciseSingleClient(new MessagesSentCounterHandler());
	}

	public void testBytesSentTwoClients() {
		exerciseTwoClients(new BytesSentCounterHandler());
	}

	public void testMessagesSentTwoClients() {
		exerciseTwoClients(new MessagesSentCounterHandler());
	}
	
	public void testBytesReceivedTwoClients() {
		exerciseTwoClients(new BytesReceivedCounterHandler());
	}

	public void testBytesSentTwoClientsInterleaved() {
		exerciseTwoClientsInterleaved(new BytesSentCounterHandler());
	}

	public void testBytesReceivedTwoClientsInterleaved() {
		exerciseTwoClientsInterleaved(new BytesReceivedCounterHandler());
	}

	private void assertTwoClients(final CounterHandler counterHandler) {
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			assertCounter(client1, new Item("bar"), counterHandler);
			assertCounter(client2, new Item("foobar"), counterHandler);
		}});
	}
	
	private void assertTwoClientsInterleaved(final CounterHandler counterHandler) {
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			double clientCount1 = storeAndReturnObservedCounters(client1, new Item("bar"), counterHandler);			
			double clientCount2 = storeAndReturnObservedCounters(client2, new Item("foobar"), counterHandler);
			_clock.advance(1000);
			
			Assert.isGreater(0, (long) clientCount1);
			Assert.isGreater(0, (long) clientCount2);
			Assert.areEqual(clientCount1, counterHandler.actualValue(client1), "Client 1");
			Assert.areEqual(clientCount2, counterHandler.actualValue(client2), "Client 2");					
		}});
	}
	
	private void assertCounter(ObjectContainer client, Item item, CounterHandler bytesSentHandler) {
		double expectedCount = storeAndReturnObservedCounters(client, item, bytesSentHandler);
		_clock.advance(1000);
		Assert.isGreater(0, (long) expectedCount);
		Assert.areEqual(expectedCount, bytesSentHandler.actualValue(client));
	}
	
	private double storeAndReturnObservedCounters(ObjectContainer client, Item item, CounterHandler handler) {		
		resetAllBeanCountersFor(client); 
		resetCountingSocket(client);
		
		client.store(item);
		return handler.expectedValue(client);
	}
	
	private void resetCountingSocket(ObjectContainer container) {
		CountingSocket4Factory countingSocketFactory = configuredSocketFactoryFor(container);

		for(CountingSocket4 socket : countingSocketFactory.countingSockets()) {
			socket.resetCount();
		}
	}
	
	private void exerciseSingleClient(CounterHandler handler) {
		for(int i = 0; i < EXERCISES_COUNT; i++) {
			assertCounter(db(), new Item("foo"), handler);
		}
	}
	
	private void exerciseTwoClients(CounterHandler handler) {
		for(int i=0; i < EXERCISES_COUNT; i++){
			assertTwoClients(handler);
		}
	}	
	
	private void exerciseTwoClientsInterleaved(CounterHandler handler) {
		for (int i=0; i < EXERCISES_COUNT; i++){
			assertTwoClientsInterleaved(handler);
		}
	}
}