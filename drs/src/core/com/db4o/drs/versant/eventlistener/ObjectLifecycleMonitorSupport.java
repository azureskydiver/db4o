/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitor.MonitorListener;
import com.db4o.foundation.*;
import com.db4o.internal.*;


public class ObjectLifecycleMonitorSupport {
	
	private static final int MONITOR_STARTUP_TIMEOUT = 10000;

	private final Thread _monitorThread;
	
	private final ObjectLifecycleMonitorImpl _monitor;

	public ObjectLifecycleMonitorSupport(EventConfiguration eventConfiguration) {
		
		_monitor = ObjectLifecycleMonitorFactory.newInstance(eventConfiguration);
		_monitorThread = new Thread(_monitor, ReflectPlatform.simpleName(ObjectLifecycleMonitorImpl.class)+" dedicated thread");
		_monitorThread.setDaemon(true);
		_monitorThread.start();
		
		final BlockingQueue4<Object> barrier = new BlockingQueue<Object>();
		_monitor.addListener(new MonitorListener() {
			
			public void ready() {
				barrier.add(new Object());
				
			}

			public void commited() {				
			}
		});

		if (barrier.next(MONITOR_STARTUP_TIMEOUT) == null) {;
			throw new IllegalStateException(ReflectPlatform.simpleName(ObjectLifecycleMonitorImpl.class) + " still not running after "+MONITOR_STARTUP_TIMEOUT+"ms");
		}
	}

	public void stop(){
		_monitor.stop();
		try {
			_monitorThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public ObjectLifecycleMonitor eventProcessor() {
		return _monitor;
	}

}
