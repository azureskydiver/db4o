/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.optional.monitoring;

import java.lang.management.*;

import javax.management.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.io.*;
import com.db4o.monitoring.*;

import db4ounit.*;

@decaf.Remove
public class MonitoredStorageTestCase implements TestCase {
	
	public void testNumSyncsPerSecond() throws Throwable {
		
		ClockMock clock = new ClockMock();
		
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		SyncCountingStorage storage = new SyncCountingStorage(new MonitoredStorage(new MemoryStorage()));
		config.file().storage(storage);
		config.common().environment().add(clock);
		
		EmbeddedObjectContainer container = Db4oEmbedded.openFile(config, null);
		try {
			
			IOStatsMBean stats = mBeanProxyFor(IOStatsMBean.class);
			
			container.store(new Object());
			container.commit();
			
			clock.advance(1000);
		
			Assert.areEqual(storage.numberOfSyncCalls(), stats.getNumSyncsPerSecond());
			
		} finally {
			container.close();
		}
		
	}

	private <T> T mBeanProxyFor(Class<T> mbeanInterface)
			throws InstanceNotFoundException, MalformedObjectNameException {
		
		MBeanServer platformServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName mBeanName = Db4oMBeans.mBeanNameFor(mbeanInterface, null);
		return JMX.newMBeanProxy(platformServer, mBeanName, mbeanInterface);
		
	}

}
