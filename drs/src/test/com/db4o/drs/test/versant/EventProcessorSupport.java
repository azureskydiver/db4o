/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class EventProcessorSupport {
	
	private final Thread _eventProcessorThread;
	
	private final EventProcessor _eventProcessor;

	public EventProcessorSupport(EventConfiguration eventConfiguration) {
		final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		PrintStream printOut = new PrintStream(byteOut);
		_eventProcessor = new EventProcessor(eventConfiguration, printOut);
		_eventProcessorThread = new Thread(new Runnable() {
			public void run() {
				_eventProcessor.run();
			}
		});
		_eventProcessorThread.start();
		boolean result = Runtime4.retry(10000, new Closure4<Boolean>() {
			public Boolean run() {
				byte[] byteArray = byteOut.toByteArray();
				String output = new String(byteArray);
				return output.contains(EventProcessor.LISTENING_MESSAGE);
			}
		});
		if(! result){
			throw new IllegalStateException("Event processor does not report '" + EventProcessor.LISTENING_MESSAGE + "'");
		}
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
