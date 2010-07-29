/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.*;
import com.db4o.foundation.*;
import com.versant.event.*;

public class EventProcessor {
	


	private final EventConfiguration _eventConfiguration;
	
	private final EventClient _client;
	
	private final VodDatabase _vod;
	
	private volatile boolean _stopped;
	
	private final StringBuilder _output = new StringBuilder();
	
	private final VodCobra _cobra;
	
	private final TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();
	
	private final BlockingQueue<ClassChannelSpec> _newChannels = new BlockingQueue<ClassChannelSpec>();
	
	private Thread _channelCreationThread = new Thread(new ChannelCreationRunnable());
	
	public EventProcessor(EventConfiguration eventConfiguration) throws IOException {
		_eventConfiguration = eventConfiguration;
		_vod = new VodDatabase(eventConfiguration.databaseName);
		
		_client = EventProcessor.newEventClient(_eventConfiguration);
		
	    _client.addExceptionListener (new ExceptionListener (){
	        public void exceptionOccurred (Throwable exception){
	        	unrecoverableExceptionOccurred(exception);
	        }
	    });
	    
	    _cobra = new VodCobra(_vod);
	    
	    _channelCreationThread.start();
	    
	    // TODO: Initialize TimestampIdGenerator
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
		
	    createMetaChannel(_client);
	    
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
		_newChannels.stop();
		
	}

	private EventChannel createMetaChannel(final EventClient client) throws IOException {
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
				long classMetadataLoid = _cobra.loidAsLong(event.getRaiserLoid());
				String className = (String)_cobra.fieldValue(classMetadataLoid, "name");
				_newChannels.add(new ClassChannelSpec(className, classMetadataLoid));
			}
			public void instanceDeleted (VersantEventObject event) {
				// do nothing
			}
		});
		return channel;
	}
	
	private void storeObjectLifeCycleEvent(VersantEventObject event, Operations operation, long classMetadataLoid) {
		long loid = VodCobra.loidAsLong(event.getRaiserLoid());
		ObjectLifecycleEvent objectLifecycleEvent = 
			new ObjectLifecycleEvent(
					classMetadataLoid,
					loid,
					operation.value,
					_timeStampIdGenerator.generate());
		_cobra.beginTransaction();
		_cobra.store(objectLifecycleEvent);
		println("Event stored: " + objectLifecycleEvent);
		_cobra.commitTransaction();
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
		if(! verbose()){
			return;
		}
		// System.out.println(msg);
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
	
	private void unrecoverableExceptionOccurred(Throwable t) {
		t.printStackTrace();
		
    	// TODO: Now what???
		
    	// Events will be broken from now on. 
    	// Maybe store some kind of BigTrouble object in the database
    	// and react to it from some daemon code in the app?
		
	}
	
	
	private final class ChannelCreationRunnable implements Runnable {

		public void run() {
			try{
				while(! _stopped){
					ClassChannelSpec channelSpec = null;
					try {
						channelSpec = _newChannels.next();
					} catch(BlockingQueueStoppedException ex){
						break; // expected, this can happen on close
					}
					final long classMetadataLoid = channelSpec._classMetadataLoid;
					final String className = channelSpec._className;
					
					String channelName = channelName(className);
					EventChannel channel = _client.getChannel (channelName);
					if (channel == null) {
						ClassChannelBuilder builder = new ClassChannelBuilder (className);
						channel = _client.newChannel (channelName, builder);
					}
					channel.addVersantEventListener (new ClassEventListener() {
						public void instanceModified (VersantEventObject event){
							storeObjectLifeCycleEvent(event, Operations.UPDATE, classMetadataLoid);
						}
						public void instanceCreated (VersantEventObject event) {
							storeObjectLifeCycleEvent(event, Operations.CREATE, classMetadataLoid);
						}
						public void instanceDeleted (VersantEventObject event) {
							storeObjectLifeCycleEvent(event, Operations.DELETE, classMetadataLoid);				
						}
					});
					println("Listener channel created for class " + className);
				}
			} catch (Exception ex){
				unrecoverableExceptionOccurred(ex);
			}
		}
	}
	
	public class ClassChannelSpec {

		public final String _className;
		
		public final long _classMetadataLoid;

		public ClassChannelSpec(String className, long classMetadataLoid) {
			_className = className;
			_classMetadataLoid = classMetadataLoid;
		}

	}

}
