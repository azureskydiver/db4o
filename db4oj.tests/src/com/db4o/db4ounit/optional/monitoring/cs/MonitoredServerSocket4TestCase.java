/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.config.*;

import db4ounit.*;

@decaf.Remove
public class MonitoredServerSocket4TestCase extends MonitoredSocket4TestCaseBase {

	private static final int PING_TIMEOUT = 10000;

	public void configureClient(Configuration config) throws Exception {
		configure(config);
	}

	public void configureServer(Configuration config) throws Exception {
		super.configure(config);
		setupNewSocketFactory(config);
	}
	
	@Override
	protected void configure(Configuration legacy) throws Exception {
		super.configure(legacy);
		ensurePingMessagesDontDisturbResults(legacy);  
	}

	private void ensurePingMessagesDontDisturbResults(Configuration legacy) {
		legacy.clientServer().timeoutClientSocket(PING_TIMEOUT);  
		legacy.clientServer().timeoutServerSocket(PING_TIMEOUT);
	}
	
	public void testBytesSentDefaultClient() {
		assertCounter(new BytesSentCounterHandler());
		//assertCounter(new BytesSentCounterHandler());
	}

	public void testBytesReceivedDefaultClient() {
		assertCounter(new BytesReceivedCounterHandler());
	}
		
	public void testMessagesSentDefaultClient() {
		assertCounter(new MessagesSentCounterHandler());
	}
	
	public void testBytesSentTwoClients() {
		assertTwoClients(new BytesSentCounterHandler());
	}
	
	public void testBytesReceivedTwoClients() {		
		assertTwoClients(new BytesReceivedCounterHandler());
	}
	
	public void testMessagesSentTwoClients() {		
		assertTwoClients(new MessagesSentCounterHandler());
	}
	
	private void assertCounter(CounterHandler handler) {
		store(new Item("default client"));
		_clock.advance(1000);
		
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(fileSession().config());
	 	CountingSocket4Factory factory= (CountingSocket4Factory) networkConfig.socketFactory();
	 	CountingSocket4 countingSocket = factory.connectedClients().get(0);
	 	
		Assert.isGreater(0, (long) handler.expectedValue(countingSocket));
		Assert.areEqual(
				handler.expectedValue(countingSocket),				
				handler.actualValue(fileSession()));
	}	
	
	private void assertTwoClients(final CounterHandler handler) {
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			CountingSocket4Factory factory = serverCountingSocketFactory();
			
			CountingSocket4 countingSocket1 = factory.connectedClients().get(1);
			CountingSocket4 countingSocket2 = factory.connectedClients().get(2);
			
			countingSocket1.resetCount();
			countingSocket2.resetCount();		 	
			
			resetBeanCountersFor(fileSession());
			
			client1.store(new Item("foo"));			
			client2.store(new Item("bar"));
			_clock.advance(1000);		
			
			Assert.areEqual(
					handler.expectedValue(countingSocket1) + handler.expectedValue(countingSocket2),
					handler.actualValue(fileSession()));			
		}});
	}

	private CountingSocket4Factory serverCountingSocketFactory() {
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(fileSession().config());
		return (CountingSocket4Factory) networkConfig.socketFactory();
	}
}
