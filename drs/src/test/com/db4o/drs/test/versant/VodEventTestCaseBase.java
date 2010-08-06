/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.foundation.*;
import com.db4o.util.IOServices.*;

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
		final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		PrintStream printOut = new PrintStream(byteOut);
		final EventProcessor eventProcessor = new EventProcessor(_vod.eventConfiguration(), printOut);
		Thread eventProcessorThread = new Thread(new Runnable() {
			public void run() {
				eventProcessor.run();
			}
		});
		eventProcessorThread.start();
		try{
			closure.run();
			boolean result = Runtime4.retry(10000, 50, new Closure4<Boolean>() {
				public Boolean run() {
					byte[] byteArray = byteOut.toByteArray();
					String output = new String(byteArray);
					return output.contains(expectedOutput);
				}
			});
			Assert.isTrue(result, "Output does not contain '" + expectedOutput + "'"); 
		} finally {
			eventProcessor.stop();
			eventProcessorThread.join();
		}
	}
	
	protected void withEventProcessor(Closure4<Void> closure) throws Exception {
		withEventProcessor(closure, "Listening");
	}

}
