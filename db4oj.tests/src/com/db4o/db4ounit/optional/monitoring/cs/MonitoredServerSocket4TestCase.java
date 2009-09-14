/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.cs.internal.config.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.events.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

@decaf.Remove
public class MonitoredServerSocket4TestCase extends MonitoredSocket4TestCaseBase {

	public void configureClient(Configuration config) throws Exception {
		configure(config);
	}

	public void configureServer(Configuration config) throws Exception {
		configure(config);
		setupNewSocketFactory(config);		
	}
	
	@Override
	protected void configure(Configuration legacy) throws Exception {
		super.configure(legacy);		
		
		//legacy.clientServer().batchMessages(false);
		//legacy.clientServer().prefetchIDCount(1);
		ensurePingMessagesDontDisturbResults(legacy);
	}

	private void ensurePingMessagesDontDisturbResults(Configuration config) {
		config.clientServer().timeoutClientSocket(Integer.MAX_VALUE);
		config.clientServer().timeoutServerSocket(Integer.MAX_VALUE);
	}
	
	public void testBytesSentDefaultClient() throws Exception {
		exerciseSingleClient(new BytesSentCounterHandler());
	}

	private void exerciseSingleClient(CounterHandler counterHandler) {
		//for (int i = 0; i < EXERCISES_COUNT; i++) {
			assertCounter(counterHandler);
		//}
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
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(fileSession().config());
		CountingSocket4Factory factory= (CountingSocket4Factory) networkConfig.socketFactory();
		
		CountingSocket4 countingSocket = factory.connectedClients().get(0);
		//countingSocket.resetCount();
		//resetAllBeanCountersFor(fileSession());
				
		store(new Item("default client"));
		_clock.advance(1000);		
	 	
		double expected = handler.expectedValue(countingSocket);
		double actual = handler.actualValue(fileSession());
		Assert.isGreater(0, (long) expected);
		Assert.areEqual(
				expected,				
				actual);
	}	
	
	private void assertTwoClients(final CounterHandler handler) {
		final IntByRef commitsReceived = new IntByRef(0);
		
		ObjectServerImpl server = (ObjectServerImpl) ((Db4oNetworking)fixture()).server().ext();
		   server.clientConnected().addListener(new EventListener4<ClientConnectionEventArgs>() {
		    public void onEvent(Event4<ClientConnectionEventArgs> e, ClientConnectionEventArgs args) {
		    	args.connection().messageReceived().addListener(new EventListener4<MessageEventArgs>() {
		    	public void onEvent(Event4<MessageEventArgs> e, MessageEventArgs args) {
		    	  if ( args.message().getClass() == MCommitSystemTransaction.class) {
		    		synchronized (commitsReceived) {
		    			commitsReceived.value++;
		    			commitsReceived.notifyAll();
					}
		    	  }
		      }
		     });
		    }
		   });		
		
		
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			synchronized (commitsReceived) {					
				while (2 != commitsReceived.value) {
					try {
						commitsReceived.wait(10);
					} catch (InterruptedException e) {
					}
				}
			}
			
			CountingSocket4Factory factory = serverCountingSocketFactory();
			
			CountingSocket4 countingSocket1 = factory.connectedClients().get(1);
			CountingSocket4 countingSocket2 = factory.connectedClients().get(2);
			
			countingSocket1.resetCount();
			countingSocket2.resetCount();			
			
			resetBeanCountersFor(fileSession());
			
			client1.store(new Item("foo"));
			client2.store(new Item("bar"));
			client1.commit();
			client2.commit();
			
			_clock.advance(1000);		
			
			double expected = handler.expectedValue(countingSocket1) + handler.expectedValue(countingSocket2);
			double actual = handler.actualValue(fileSession());

			Assert.areEqual(
					expected,
					actual);			
		}});
	}

	private CountingSocket4Factory serverCountingSocketFactory() {
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(fileSession().config());
		return (CountingSocket4Factory) networkConfig.socketFactory();
	}
}
