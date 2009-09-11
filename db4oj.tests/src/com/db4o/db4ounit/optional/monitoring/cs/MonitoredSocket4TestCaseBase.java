/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;

import javax.management.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.config.*;
import com.db4o.db4ounit.optional.monitoring.*;
import com.db4o.ext.*;
import com.db4o.monitoring.*;
import com.db4o.monitoring.cs.*;

import db4ounit.extensions.fixtures.*;

@decaf.Ignore
public abstract class MonitoredSocket4TestCaseBase extends MBeanTestCaseBase implements OptOutAllButNetworkingCS , CustomClientServerConfiguration {

	@Override
	protected Class<?> beanInterface() {
		return NetworkingMBean.class;
	}

	@Override
	protected String beanUri() {
		return db().toString();
	}

	protected CountingSocket4Factory setupNewSocketFactory(Configuration config) {
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(config);
		
		CountingSocket4Factory socketFactory = new CountingSocket4Factory(new MonitoredSocket4Factory(networkConfig.socketFactory()));
		networkConfig.socketFactory(socketFactory);
		
		return socketFactory;
	}

	protected CountingSocket4Factory configuredSocketFactoryFor(ObjectContainer container) {
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(container.ext().configure());
		CountingSocket4Factory factory = (CountingSocket4Factory) networkConfig.socketFactory();
		return factory;
	}

	protected void withTwoClients(TwoClientsAction action) {
		ExtObjectContainer client1 = null;
		ExtObjectContainer client2 = null;
		
		try {	
			client1 = openNewSession();
			client2 = openNewSession();
				
			action.apply(client1, client2);				
		} finally {
			if (client1 != null) client1.close();
			if (client2 != null) client2.close();			
		}
	}

	protected interface TwoClientsAction {
		void apply(ObjectContainer client1, ObjectContainer client2);
	}
	
	protected void resetAllBeanCountersFor(ObjectContainer...  clients) {
		for(ObjectContainer container : clients) {
			resetBeanCountersFor(container);
		}
	}

	protected void resetBeanCountersFor(ObjectContainer container) {
		ObjectName objectName = Db4oMBeans.mBeanNameFor(NetworkingMBean.class, container.toString());
		MBeanProxy bean = new MBeanProxy(objectName);
		
		bean.resetCounters();
	}
	
	protected class BytesSentCounterHandler extends CounterHandlerBase {
		public double actualValue(ObjectContainer container) {
			return getAttribute(container.toString(), "BytesSentPerSecond") ;
		}

		public double expectedValue(CountingSocket4 countingSocket) {
			return countingSocket.bytesSent();
		}
	}
	
	protected class BytesReceivedCounterHandler extends CounterHandlerBase {
		public double actualValue(ObjectContainer container) {
			return getAttribute(container.toString(), "BytesReceivedPerSecond") ;
		}

		public double expectedValue(CountingSocket4 countingSocket) {
			return countingSocket.bytesReceived();
		}
	}
	
	protected class MessagesSentCounterHandler extends CounterHandlerBase {
		public double actualValue(ObjectContainer container) {
			return getAttribute(container.toString(), "MessagesSentPerSecond") ;
		}

		public double expectedValue(CountingSocket4 countingSocket) {
			return countingSocket.messagesSent();
		}
	}
	
	protected abstract class CounterHandlerBase implements CounterHandler {
		
		public double expectedValue(ObjectContainer container) {
			return observedCounter(container);
		}
		
		protected double getAttribute(String beanUri, String attribute) {
			ObjectName objectName = Db4oMBeans.mBeanNameFor(NetworkingMBean.class, beanUri);
			MBeanProxy bean = new MBeanProxy(objectName);
			return bean.<Double>getAttribute(attribute);		
		}
		
		private double observedCounter(ObjectContainer container) {
			CountingSocket4Factory factory = configuredSocketFactoryFor(container);
			
			double total = 0.0;
			for (CountingSocket4 socket :factory.countingSockets()) {
				double expectedValue = expectedValue(socket);
				total += expectedValue;
			}
			
			return total;
		}
	}
	
	interface CounterHandler {
		
		double expectedValue(CountingSocket4 socket);
		
		double expectedValue(ObjectContainer container);

		double actualValue(ObjectContainer container);

	}

	protected CountingSocket4Factory _socket4Factory;
}
