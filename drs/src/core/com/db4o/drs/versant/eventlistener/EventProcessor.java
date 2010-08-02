/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;
import java.util.*;

import com.db4o.drs.versant.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.*;
import com.db4o.foundation.*;
import com.versant.event.*;

public class EventProcessor {
	
	private final int COMMIT_INTERVAL = 1000; // 1 sec
	
	private final EventConfiguration _eventConfiguration;
	
	private final PrintStream _out;
	
	private final EventClient _client;
	
	private volatile boolean _stopped;
	
	private final VodCobra _cobra;
	
	private final TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();
	
	private final BlockingQueue<ClassChannelSpec> _newChannels = new BlockingQueue<ClassChannelSpec>();
	
	private volatile boolean _dirty;
	
	private final Object _lock = new Object();
	
	SimpleTimer _commitTimer = new SimpleTimer(
			new Runnable() {
				public void run() {
					commit();
				}}, 
			COMMIT_INTERVAL, 
			"EventProcessor Commit");
	
	private Thread _commitThread = new Thread(_commitTimer);
	
	private long _timestampLoid;
	
	public EventProcessor(EventConfiguration eventConfiguration, PrintStream out) throws IOException {
		_eventConfiguration = eventConfiguration;
		_out = out;
		
		_client = EventProcessor.newEventClient(_eventConfiguration);
		
	    _client.addExceptionListener (new ExceptionListener (){
	        public void exceptionOccurred (Throwable exception){
	        	unrecoverableExceptionOccurred(exception);
	        }
	    });
	    
	    _cobra = new VodCobra(new VodDatabase(eventConfiguration.databaseName));
	    
	    _commitThread.start();
	    
	    
	    try{
	    	produceLastTimestamp();
	    } catch (Exception ex){
	    	unrecoverableExceptionOccurred(ex);
	    }
	    
	    startChannelsFromKnownClasses();
	}

	private void startChannelsFromKnownClasses() throws IOException {
		Collection<Long> classMetadataLoids = _cobra.loids(ClassMetadata.class);
	    for (Long loid : classMetadataLoids) {
	    	ClassMetadata classMetadata = _cobra.objectByLoid(loid);
	    	createChannel(new ClassChannelSpec(classMetadata.name(), loid));
		}
	}

	private void produceLastTimestamp() throws Exception {
		Collection<Long> timestampLoids = _cobra.loids(CommitTimestamp.class);
	    
	    switch(timestampLoids.size()){
	    
	    	case 0:
		    	_timestampLoid = _cobra.store(new CommitTimestamp(0));
		    	_cobra.commit();
		    	return;
	    	case 1:
	    		_timestampLoid = timestampLoids.iterator().next();
	    		CommitTimestamp timestamp = _cobra.objectByLoid(_timestampLoid);
	    		System.out.println("Timestamp read: " + timestamp.value());
	    		_timeStampIdGenerator.setMinimumNext(timestamp.value());
	    		return;
	    	default:
	    		throw new IllegalStateException("Multiple CommitTimestamp instances in database");
	    }
	    
	}

	public static EventClient newEventClient(EventConfiguration config) throws IOException {
		try{
			return new EventClient(config.serverHost,config.serverPort,config.clientHost,config.clientPort,config.databaseName);
		} catch (IOException ioException){
			System.err.println("Connection failed using\n" + config);
			ioException.printStackTrace();
			throw ioException;
		}
	}

	public void run() throws IOException {
	    createMetaChannel(_client);
	    println("Listening for events on " + _eventConfiguration.databaseName);
	    channelCreationLoop();
		shutdown();
	}

	private void shutdown() {
		_client.shutdown();
		_commitTimer.stop();
		try {
			_commitThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		commit();
		_cobra.close();
	}

	private void channelCreationLoop() {
		try{
			while(! _stopped){
				try {
					createChannel(_newChannels.next());
				} catch(BlockingQueueStoppedException ex){
					break;
				}
			}
		} catch (Exception ex){
			unrecoverableExceptionOccurred(ex);
		}
	}
	
	private void createChannel(final ClassChannelSpec channelSpec) throws IOException{
		String channelName = channelName(channelSpec._className);
		EventChannel channel = _client.getChannel (channelName);
		if (channel == null) {
			ClassChannelBuilder builder = new ClassChannelBuilder (channelSpec._className);
			channel = _client.newChannel (channelName, builder);
		}
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceModified (VersantEventObject event){
				storeObjectLifeCycleEvent(event, Operations.UPDATE, channelSpec);
			}
			public void instanceCreated (VersantEventObject event) {
				storeObjectLifeCycleEvent(event, Operations.CREATE, channelSpec);
			}
			public void instanceDeleted (VersantEventObject event) {
				storeObjectLifeCycleEvent(event, Operations.DELETE, channelSpec);				
			}
		});
		println("Listener channel created for class " + channelSpec._className);
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
				synchronized(_lock){
					long classMetadataLoid = VodCobra.loidAsLong(event.getRaiserLoid());
					String className = (String)_cobra.fieldValue(classMetadataLoid, "name");
					_newChannels.add(new ClassChannelSpec(className, classMetadataLoid));
					_dirty = true;
				}
			}
			public void instanceDeleted (VersantEventObject event) {
				// do nothing
			}
		});
		return channel;
	}
	
	private void storeObjectLifeCycleEvent(VersantEventObject event, Operations operation, ClassChannelSpec channelSpec) {
		synchronized (_lock) {
			long loid = VodCobra.loidAsLong(event.getRaiserLoid());
			ObjectLifecycleEvent objectLifecycleEvent = 
				new ObjectLifecycleEvent(
						channelSpec._classMetadataLoid,
						loid,
						operation.value,
						_timeStampIdGenerator.generate());
			_cobra.store(objectLifecycleEvent);
			println("Event stored: " + objectLifecycleEvent);
			_dirty = true;
		}
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
		if(! _eventConfiguration.verbose){
			return;
		}
		synchronized (_lock) {
			_out.println(msg);
		}
	}
	
	public void stop(){
		_stopped = true;
		_newChannels.stop();
	}

	private void unrecoverableExceptionOccurred(Throwable t) {
		t.printStackTrace();
    	// TODO: Now what???
    	// Events will be broken from now on. 
    	// Maybe store some kind of BigTrouble object in the database
    	// and react to it from some daemon code in the app?
	}
	
	private void commit() {
		synchronized(_lock){
			if(_dirty){
				_cobra.store(_timestampLoid, new CommitTimestamp(_timeStampIdGenerator.last()));
				_cobra.commit();
				_dirty = false;
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
