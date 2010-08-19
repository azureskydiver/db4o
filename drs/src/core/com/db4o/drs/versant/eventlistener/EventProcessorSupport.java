/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;

import com.db4o.drs.foundation.*;
import com.db4o.drs.versant.*;
import com.db4o.foundation.*;


public class EventProcessorSupport {
	
	private final Thread _eventProcessorThread;
	
	private final EventProcessor _eventProcessor;

	private final ByteArrayOutputStream _byteOut;

	public EventProcessorSupport(EventConfiguration eventConfiguration) {
		
		// TODO: Not sure is the ByteArrayOutputStream approach scales for
		// large number of objects.
		// The ByteArrayOutputStream could bloat memory.
		// So far we only reset on the waitForOutput.
		_byteOut = new ByteArrayOutputStream();
		PrintStream printOut = new PrintStream(_byteOut);
		
		_eventProcessor = EventProcessorFactory.newInstance(eventConfiguration, LinePrinter.forPrintStream(printOut));
		_eventProcessorThread = new Thread(new Runnable() {
			public void run() {
				_eventProcessor.run();
			}
		}, EventProcessor.class.getSimpleName()+" dedicated thread");
		_eventProcessorThread.start();
		if(! waitForOutput(EventProcessor.LISTENING_MESSAGE)){
			throw new IllegalStateException("Event processor does not report '" + EventProcessor.LISTENING_MESSAGE + "'");
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
		_eventProcessor.stop();
		try {
			_eventProcessorThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
