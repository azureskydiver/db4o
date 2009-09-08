/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;

import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.cs.config.NetworkingConfiguration;
import com.db4o.cs.internal.config.Db4oClientServerLegacyConfigurationBridge;

import db4ounit.Assert;

@decaf.Remove
public class MonitoredServerSocket4TestCase extends MonitoredSocket4TestCaseBase {

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
	}
	
	public void testDefaultClient() {
		store(new Item("default client"));
		_clock.advance(1000);
		
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(fileSession().config());
	 	CountingSocket4Factory factory= (CountingSocket4Factory) networkConfig.socketFactory();
	 	double bytesSent = factory.connectedClients().get(0).getBytesSent();
		
		Assert.isGreater(0, (long) bytesSent);
		Assert.areEqual(
				bytesSent,				
				getAttribute(fileSession().toString(), "BytesSentPerSecond"));
	}
	
	public void testTwoClients() {		
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			CountingSocket4Factory factory = serverCountingSocketFactory();
			
			CountingSocket4 countingSocket1 = factory.connectedClients().get(1);
			CountingSocket4 countingSocket2 = factory.connectedClients().get(2);
			
			countingSocket1.resetCount();
			countingSocket2.resetCount();		 	
			getBytesSentPerSecond(fileSession()); // reset server
			resetBeanCounterFor(fileSession());
			
			client1.store(new Item("foo"));			
			client2.store(new Item("bar"));
			_clock.advance(1000);
			
			Assert.areEqual(
						countingSocket1.getBytesSent() + countingSocket2.getBytesSent(), 
						getBytesSentPerSecond(fileSession()));
		}});
	}

	private CountingSocket4Factory serverCountingSocketFactory() {
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(fileSession().config());
		return (CountingSocket4Factory) networkConfig.socketFactory();
	}
}
