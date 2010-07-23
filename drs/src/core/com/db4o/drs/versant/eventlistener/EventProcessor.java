/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;

import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.metadata.*;
import com.versant.event.*;

public class EventProcessor {
	
	private final EventConfiguration _eventConfiguration;
	
	private volatile boolean _stopped;
	
	private final StringBuilder _output = new StringBuilder();

	public EventProcessor(EventConfiguration eventConfiguration) {
		_eventConfiguration = eventConfiguration;
		
	}

	public static EventClient newEventClient(EventConfiguration config) throws IOException {
		try{
			return new EventClient(
					config.serverHost,
					config.serverPort,
					config.clientHost,
					config.clientPort,
					config.databaseName);
		} catch (IOException ioException){
			System.err.println("Connection failed using\n" + config);
			throw ioException;
		}
	}

	public void run() throws IOException {
		
		EventClient client = EventProcessor.newEventClient(_eventConfiguration);
		
			
		    ExceptionListener exception_listener = new ExceptionListener (){
		        public void exceptionOccurred (Throwable exception){
		            exception.printStackTrace ();
		        }
		    };
		    
		    client.addExceptionListener (exception_listener);
		    
		    EventChannel channel1 = createChannel(client, ClassMetadata.class.getName());
		    EventChannel channel2 = createChannel(client, Item.class.getSimpleName());
		    // createChannel(client, Item.class.getName());
		    
		    println("Listening for events on " + _eventConfiguration.databaseName);
		
		
		
		try {
			while(! _stopped){
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.shutdown();
		
	}

	private EventChannel createChannel(EventClient client, final String className) throws IOException {
		
		String channelName = "dRS_Channel_For_" + className;
		channelName = channelName.replaceAll("\\.", "");
		int beginIndex = channelName.length() - 32;
		if(beginIndex > 0){
			channelName = channelName.substring(beginIndex);
		}

		System.out.println(channelName);
		
		EventChannel channel = client.getChannel (channelName);
		if (channel == null) {
		    
			ClassChannelBuilder builder = new ClassChannelBuilder (className);
		    channel = client.newChannel (channelName, builder);
		}

		channel.addVersantEventListener (new ClassEventListener() {
			
			public void instanceModified (VersantEventObject event){
				println(Item.class.getName());
				println(className);
			}

			public void instanceCreated (VersantEventObject event) {
				println(Item.class.getName());
				println(className);
			}

			public void instanceDeleted (VersantEventObject event) {
				println(Item.class.getName());
				println(className);
			}
			
		});
		System.out.println("Channel is up for: " + channelName);
		return channel;
	}

	private synchronized void println(String msg) {
//		if(! verbose()){
//			return;
//		}
		System.out.println(msg);
		_output.append(msg);
		_output.append("\n");
	}
	
	public void stop(){
		_stopped = true;
	}

	private boolean verbose() {
		return _eventConfiguration.verbose;
	}

	public synchronized boolean outputContains(String name) {
		return _output.indexOf(name) >= 0;
	}

}
