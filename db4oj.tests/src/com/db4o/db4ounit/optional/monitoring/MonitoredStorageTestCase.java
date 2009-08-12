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
	
	private final ObjectName _beanName = getIOMBeanName();

	private final MBeanServer _platformServer = ManagementFactory.getPlatformMBeanServer();

	public void testNumSyncsPerSecond() {
		Assert.areEqual(_storage.numberOfSyncCalls(), getSyncsPerSecond());		
	}

	private double getSyncsPerSecond() {
		return getAttribute("SyncsPerSecond");
	}

	private double getAttribute(final String attribute) {
		try {
			return (Double)_platformServer.getAttribute(_beanName, attribute);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public void testNumBytesReadPerSecond() {
		Assert.areEqual(_storage.numberOfBytesRead(), getBytesReadPerSecond());		
	}

	private double getBytesReadPerSecond() {
		return getAttribute("BytesReadPerSecond");
	}

	public void testNumBytesWrittenPerSecond() {
		Assert.areEqual(_storage.numberOfBytesWritten(), getBytesWrittenPerSecond());		
	}

	private double getBytesWrittenPerSecond() {
		return getAttribute("BytesWrittenPerSecond");
	}

	public void testNumReadsPerSecond() {
		Assert.areEqual(_storage.numberOfReadCalls(), getReadsPerSecond());		
	}

	private double getReadsPerSecond() {
		return getAttribute("ReadsPerSecond");
	}

	public void testNumWritesPerSecond() {
		Assert.areEqual(_storage.numberOfWriteCalls(), getWritesPerSecond());		
	}

	private double getWritesPerSecond() {
		return getAttribute("WritesPerSecond");
	}

	public void setUp() throws Exception{
		ClockMock clock = new ClockMock();
		
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(_storage);
		config.common().environment().add(clock);
		
		_container = Db4oEmbedded.openFile(config, null);
		_container.store(new Object());
		_container.commit();
		
		clock.advance(1000);
	
	}

	public void tearDown() throws Exception {
		if(null != _container){
			_container.close();
		}
	}
	
	private ObjectName getIOMBeanName() {
		try {
			return Db4oMBeans.mBeanNameFor(IOMBean.class, null);
		} catch (MalformedObjectNameException e) {
			throw new IllegalStateException(e);
		}
	}

}
