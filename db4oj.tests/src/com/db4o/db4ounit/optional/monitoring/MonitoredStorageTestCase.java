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
public class MonitoredStorageTestCase implements TestLifeCycle {
	
	private CountingStorage _storage = new CountingStorage(new MonitoredStorage(new MemoryStorage()));
	
	private EmbeddedObjectContainer _container;
	
	private IOMBean _mBean;

	public void testNumSyncsPerSecond() {
		Assert.areEqual(_storage.numberOfSyncCalls(), _mBean.getSyncsPerSecond());		
	}

	public void testNumBytesReadPerSecond() {
		Assert.areEqual(_storage.numberOfBytesRead(), _mBean.getBytesReadPerSecond());		
	}

	public void testNumBytesWrittenPerSecond() {
		Assert.areEqual(_storage.numberOfBytesWritten(), _mBean.getBytesWrittenPerSecond());		
	}

	public void testNumReadsPerSecond() {
		Assert.areEqual(_storage.numberOfReadCalls(), _mBean.getReadsPerSecond());		
	}

	public void testNumWritesPerSecond() {
		Assert.areEqual(_storage.numberOfWriteCalls(), _mBean.getWritesPerSecond());		
	}

	public void setUp() throws Exception{
		ClockMock clock = new ClockMock();
		
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(_storage);
		config.common().environment().add(clock);
		
		_container = Db4oEmbedded.openFile(config, null);
		
		_mBean = mBeanProxyFor(IOMBean.class);
		
		_container.store(new Object());
		_container.commit();
		
		clock.advance(1000);
	
	}

	private <T> T mBeanProxyFor(Class<T> mbeanInterface)
			throws InstanceNotFoundException, MalformedObjectNameException {
		
		MBeanServer platformServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName mBeanName = Db4oMBeans.mBeanNameFor(mbeanInterface, null);
		return JMX.newMBeanProxy(platformServer, mBeanName, mbeanInterface);
		
	}

	public void tearDown() throws Exception {
		if(null != _container){
			_container.close();
		}
	}

}
