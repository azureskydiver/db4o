/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.metadata.*;
import com.versant.event.*;

public class EventProcessor {
	
	private final EventConfiguration _eventConfiguration;
	
	private final EventClient _client;
	
	private final VodDatabase _vod;
	
	private volatile boolean _stopped;
	
	private final StringBuilder _output = new StringBuilder();
	
	private final VodCobra _cobra;

	public EventProcessor(EventConfiguration eventConfiguration) throws IOException {
		_eventConfiguration = eventConfiguration;
		_vod = new VodDatabase(eventConfiguration.databaseName);
		
		_client = EventProcessor.newEventClient(_eventConfiguration);
		
	    _client.addExceptionListener (new ExceptionListener (){
	        public void exceptionOccurred (Throwable exception){
	        	
	        	// TODO: Now what??? 
	        	// Events will be broken from now on. 
	        	// Maybe store some kind of BigTrouble object in the database
	        	// and react to it from some daemon code in the app?
	            exception.printStackTrace ();
	        }
	    });
	    
	    _cobra = new VodCobra(_vod);
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
		
	    EventChannel channel = createMetaChannel(_client);
	    
	    println("Listening for events on " + _eventConfiguration.databaseName);
		
		try {
			while(! _stopped){
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			
		}
		
		shutdown();
		
	}

	private void shutdown() {
		_client.shutdown();
		_cobra.close();
	}

	private EventChannel createMetaChannel(EventClient client) throws IOException {
		String className = ClassMetadata.class.getName();
		String channelName = channelName(className);
		EventChannel channel = client.getChannel (channelName);
		
		
		
		if (channel == null) {
			ClassChannelBuilder builder = new ClassChannelBuilder (className);
		    channel = client.newChannel (channelName, builder);
		}
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceModified (VersantEventObject event){
				// do nothing
			}
			public void instanceCreated (VersantEventObject event) {
				String className = (String)_cobra.fieldValue(event.getRaiserLoid(), "fullyQualifiedName");
				println(className);
				
				// println(className);
				
			}
			public void instanceDeleted (VersantEventObject event) {
				// do nothing
			}
		});
		return channel;
	}
	
	private EventChannel createClassChannel(EventClient client, final String className) throws IOException {
		String channelName = channelName(className);
		EventChannel channel = client.getChannel (channelName);
		if (channel == null) {
			ClassChannelBuilder builder = new ClassChannelBuilder (className);
		    channel = client.newChannel (channelName, builder);
		}
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceModified (VersantEventObject event){
				println(className);
			}
			public void instanceCreated (VersantEventObject event) {
				println(className);
			}
			public void instanceDeleted (VersantEventObject event) {
				println(className);
			}
		});
		return channel;
	}


	private String channelName(final String className) {
		String name =  "dRS_Channel_For_" + className;
		name = name.replaceAll("\\.", "");
		int beginIndex = name.length() - 32;
		if(beginIndex > 0){
			name = name.substring(beginIndex);
		}
		return name;

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
