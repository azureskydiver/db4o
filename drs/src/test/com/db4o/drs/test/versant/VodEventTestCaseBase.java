/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.inband.*;
import com.db4o.foundation.*;
import com.db4o.util.IOServices.*;
import com.versant.event.*;

import db4ounit.*;

public class VodEventTestCaseBase extends VodProviderTestCaseBase{
	
	protected void withEventProcessor(Closure4<Void> closure, String expectedOutput) throws Exception {
		if(DrsDebug.runEventListenerEmbedded){
			withEventProcessorInSameProcess(closure, expectedOutput);
		} else {
			withEventProcessorInSeparateProcess(closure, expectedOutput);	
		}
	}
	
	private void withEventProcessorInSeparateProcess (Closure4<Void> closure, final String expectedOutput) throws Exception {
		final ProcessRunner eventListenerProcess = _vod.startEventProcessorInSeparateProcess();
		try{
			closure.run();
			boolean result = Runtime4.retry(10000, new Closure4<Boolean>() {
				public Boolean run() {
					return eventListenerProcess.outputContains(expectedOutput);
				}
			});
			Assert.isTrue(result, "Output does not contain '" + expectedOutput + "'"); 
		} finally {
			eventListenerProcess.destroy();
		}
	}
	
	private void withEventProcessorInSameProcess (Closure4<Void> closure, final String expectedOutput) throws Exception {
		
		// TODO: This will not really scale for large number of objects.
		//       The ByteArrayOutputStream will bloat memory.
		final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		
		PrintStream printOut = new PrintStream(byteOut);
		VodCobra cobra = new VodCobra(new VodDatabase(_vod.eventConfiguration().databaseName));
		VodEventClient client = new VodEventClient(_vod.eventConfiguration(), new ExceptionListener (){
	        public void exceptionOccurred (Throwable exception){
	        	EventProcessor.unrecoverableExceptionOccurred(exception);
	        }
	    });
		Object lock = new Object();
		EventProcessorSideCommunication comm = new InBandEventProcessorSideCommunication(cobra, client, lock);
		final EventProcessor eventProcessor = new EventProcessor(client, _vod.eventConfiguration(), printOut, cobra, comm, lock);
		Thread eventProcessorThread = new Thread(new Runnable() {
			public void run() {
				eventProcessor.run();
			}
		});
		eventProcessorThread.start();
		try{
			if(! waitForOutputToContain(byteOut, EventProcessor.LISTENING_MESSAGE)){
				throw new IllegalStateException("EventProcessor does not report to be up. No message '" + EventProcessor.LISTENING_MESSAGE + "'");
			}
			closure.run();
			Assert.isTrue(waitForOutputToContain(byteOut, expectedOutput), "Output does not contain '" + expectedOutput + "'"); 
		} finally {
			eventProcessor.stop();
			eventProcessorThread.join();
			printOut.close();
		}
	}

	private boolean waitForOutputToContain(final ByteArrayOutputStream byteOut, final String expectedOutput) {
		return Runtime4.retry(100000, new Closure4<Boolean>() {
			public Boolean run() {
				return new String(byteOut.toByteArray()).contains(expectedOutput);
			}
		});
	}
	
	protected void withEventProcessor(Closure4<Void> closure) throws Exception {
		withEventProcessor(closure, EventProcessor.LISTENING_MESSAGE);
	}

}
