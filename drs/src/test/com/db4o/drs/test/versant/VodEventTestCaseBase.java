/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.io.*;
import java.util.*;


import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.eventlistener.Program.*;
import com.db4o.foundation.*;
import com.db4o.util.*;
import com.db4o.util.IOServices.*;

import db4ounit.*;

public class VodEventTestCaseBase extends VodProviderTestCaseBase{
	
	protected void withEventProcessor(Closure4<Void> closure, String expectedOutput) throws Exception {
		withEventProcessorInSameProcess(closure, expectedOutput);
		// withEventProcessorInSeparateProcess(closure, expectedOutput);
	}
	
	private void withEventProcessorInSeparateProcess (Closure4<Void> closure, final String expectedOutput) throws Exception {
		final ProcessRunner eventListenerProcess = startEventListenerProcess();
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
		final EventProcessor eventProcessor = new EventProcessor(newEventConfiguration(), printOut);
		Thread eventProcessorThread = new Thread(new Runnable() {
			public void run() {
				eventProcessor.run();
			}
		});
		eventProcessorThread.start();
		try{
			closure.run();
			boolean result = Runtime4.retry(10000, new Closure4<Boolean>() {
				public Boolean run() {
					byte[] byteArray = byteOut.toByteArray();
					String output = new String(byteArray);
					// System.out.println(output);
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
	
	protected ProcessRunner startEventListenerProcess() throws IOException {
		
		List<String> arguments = new ArrayList<String>();
		
		addArgument(arguments, Arguments.DATABASE, DATABASE_NAME);
		addArgument(arguments, Arguments.LOGFILE, EVENT_LOGFILE_NAME);
		addArgument(arguments, Arguments.SERVER_PORT, SERVER_PORT);
		addArgument(arguments, Arguments.CLIENT_PORT, CLIENT_PORT);
		addArgument(arguments, Arguments.DATABASE, DATABASE_NAME);
		
		String[] argumentsAsString = new String[arguments.size()];
		argumentsAsString = arguments.toArray(argumentsAsString);
		
		ProcessRunner eventListenerProcess = JavaServices.startJava(Program.class.getName(), argumentsAsString);
		eventListenerProcess.checkIfStarted(DATABASE_NAME, 10000);
		return eventListenerProcess;
	}
	
	private static void addArgument(List<String> arguments, String argumentName, int argumentValue) {
		addArgument(arguments, argumentName, String.valueOf(argumentValue));
	}

	private static void addArgument(List<String> arguments, String argumentName, String argumentValue) {
		addArgument(arguments, argumentName);
		arguments.add(argumentValue);
	}
	
	private static void addArgument(List<String> arguments, String argumentName) {
		arguments.add("-" + argumentName);
	}


}
