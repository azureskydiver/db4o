/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant.eventlistener;

import java.io.*;
import java.util.*;

import javax.jdo.*;

import com.db4o.drs.test.versant.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.eventlistener.Program.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.*;
import com.db4o.foundation.*;
import com.db4o.util.*;
import com.db4o.util.IOServices.*;

import db4ounit.*;


public class EventListenerIntegrationTestCase extends VodEventTestCaseBase {
	

	public void _testListenerStartAndStop() throws Exception{
		VodEventDriver eventDriver = startEventDriver();
		try {
			ProcessRunner eventListenerProcess = startListener();
			eventListenerProcess.destroy();
		} finally {
			eventDriver.stop();
		}
	}

	
	public void testStoreSingleObject() throws Exception {
		VodEventDriver eventDriver = startEventDriver();
		try {
//			final ProcessRunner eventListenerProcess = startListener();
			
			final EventProcessor eventProcessor = new EventProcessor(newEventConfiguration(), System.out);
			Thread eventProcessorThread = new Thread(new Runnable() {
				public void run() {
					try {
						eventProcessor.run();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			eventProcessorThread.start();
			try{
				boolean result = Runtime4.retry(10000, new Closure4<Boolean>() {
					public Boolean run() {
						Item item = new Item("one");
						_provider.storeNew(item);
						_provider.commit();
						
						final long objectLoid = _provider.loid(item);
						
						return _jdo.transactional(new Closure4<Boolean>() {
							public Boolean run() {
								Query query = _pm.newQuery(ObjectLifecycleEvent.class, "this.objectLoid == param");
								query.declareParameters("long param");
								Collection<ObjectLifecycleEvent> objectLifecycleEvents = (Collection<ObjectLifecycleEvent>) query.execute(objectLoid);
								if(objectLifecycleEvents.size() != 1){
									return false;
								}
								ObjectLifecycleEvent objectLifecycleEvent = objectLifecycleEvents.iterator().next();
								Assert.areEqual(Operations.CREATE.value, objectLifecycleEvent.operation());
								Assert.isGreater(1, objectLifecycleEvent.timestamp());
								Assert.isGreater(1, objectLifecycleEvent.classMetadataLoid());
								return true;
							}
						});
					}
				});
				Assert.isTrue(result, "Timeout: ObjectLifecycleEvent object not stored.");
				
			} finally {
//				eventListenerProcess.destroy();
				eventProcessor.stop();
				eventProcessorThread.join();
			}
		}finally {
			eventDriver.stop();
		}
	}


	
	private ProcessRunner startListener() throws IOException {
		
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
