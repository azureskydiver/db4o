/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;

import com.db4o.drs.foundation.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;


public class EventProcessorSupport {
	
	private final Thread _monitorThread;
	
	private final ObjectLifecycleMonitorImpl _monitor;

	private final ByteArrayOutputStream _byteOut;

	public EventProcessorSupport(EventConfiguration eventConfiguration) {
		
		// TODO: Not sure is the ByteArrayOutputStream approach scales for
		// large number of objects.
		// The ByteArrayOutputStream could bloat memory.
		// So far we only reset on the waitForOutput.
		_byteOut = new ByteArrayOutputStream();
		PrintStream printOut = new PrintStream(_byteOut);
		
		_monitor = ObjectLifecycleMonitorFactory.newInstance(eventConfiguration, LinePrinter.forPrintStream(printOut));
		_monitorThread = new Thread(_monitor, ReflectPlatform.simpleName(ObjectLifecycleMonitorImpl.class)+" dedicated thread");
		_monitorThread.setDaemon(true);
		_monitorThread.start();
		if(! waitForOutput(ObjectLifecycleMonitorImpl.LISTENING_MESSAGE)){
			throw new IllegalStateException("Event processor does not report '" + ObjectLifecycleMonitorImpl.LISTENING_MESSAGE + "'");
		}
	}

	public boolean waitForOutput(final String string) {
		boolean result = Runtime4.retry(10000, new Closure4<Boolean>() {
			public Boolean run() {
				return outputContains(string);
			}
		});
		_byteOut.reset();
		return result;
	}
	
	private boolean outputContains(String string) {
		byte[] byteArray = _byteOut.toByteArray();
		String output = new String(byteArray);
		return output.contains(string);
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
