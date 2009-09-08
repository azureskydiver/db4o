/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.optional.monitoring.cs;

import javax.management.ObjectName;

import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.cs.config.NetworkingConfiguration;
import com.db4o.cs.internal.config.Db4oClientServerLegacyConfigurationBridge;
import com.db4o.db4ounit.optional.monitoring.*;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.monitoring.Db4oMBeans;
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
	
	protected double observedBytesSent(ObjectContainer container) {
		CountingSocket4Factory factory = configuredSocketFactoryFor(container);
		
		double total = 0.0;
		for (CountingSocket4 socket :factory.countingSockets()) {
			total += socket.getBytesSent();
		}
		
		return total;
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

	protected double getAttribute(String beanUri, String attribute) {
		ObjectName objectName = Db4oMBeans.mBeanNameFor(NetworkingMBean.class, beanUri);
		MBeanProxy bean = new MBeanProxy(objectName);
		return bean.<Double>getAttribute(attribute);		
	}

	protected double getBytesSentPerSecond(ObjectContainer client) {
		return getAttribute(client.toString(), "BytesSentPerSecond");
	}

	protected void resetBeanCounterFor(ObjectContainer...  clients) {
		for(ObjectContainer container : clients) {
			getBytesSentPerSecond(container); // reading to reset bean counter.
		}
	}

	protected CountingSocket4Factory _socket4Factory;
}
