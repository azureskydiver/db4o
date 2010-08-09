/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;
import java.util.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.*;
import com.db4o.foundation.*;
import com.versant.event.*;

public class EventProcessor {
	
	public static final String LISTENING_MESSAGE = "Listening for events on ";
	
	public static final String COMMIT_MESSAGE = "EventProcessor commit";

	private final int COMMIT_INTERVAL = 1000; // 1 sec
	
	private final EventConfiguration _eventConfiguration;
	
	private final PrintStream _out;
	
	private final EventClient _client;
	
	private volatile boolean _stopped;
	
	private final VodCobra _cobra;
	
	private final TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();
	
	private final BlockingQueue<Block4> _queuedTasks = new BlockingQueue<Block4>();
	
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
	
	public EventProcessor(EventConfiguration eventConfiguration, PrintStream out)  {
		_eventConfiguration = eventConfiguration;
		_out = out;
		
		_client = EventProcessor.newEventClient(_eventConfiguration);
		
	    _client.addExceptionListener (new ExceptionListener (){
	        public void exceptionOccurred (Throwable exception){
	        	unrecoverableExceptionOccurred(exception);
	        }
	    });
	    
	    _cobra = new VodCobra(new VodDatabase(eventConfiguration.databaseName));
	    
	    synchronized(_lock){
		    _commitThread.start();
		    
		    try{
		    	produceLastTimestamp();
		    } catch (Exception ex){
		    	unrecoverableExceptionOccurred(ex);
		    }
		    
		    startChannelsFromKnownClasses();
	    }
	}

	private void startChannelsFromKnownClasses() {
		Collection<Long> classMetadataLoids = _cobra.loids(ClassMetadata.class);
	    for (Long loid : classMetadataLoids) {
	    	ClassMetadata classMetadata = _cobra.objectByLoid(loid);
	    	createChannel(new ClassChannelSpec(classMetadata.name(), classMetadata.fullyQualifiedName(),  loid));
		}
	}

	private void produceLastTimestamp() throws Exception {
		Long timestampLoid = _cobra.singleInstanceLoid(CommitTimestamp.class);
		if(timestampLoid == null) {
	    	_timestampLoid = _cobra.store(new CommitTimestamp(0));
	    	_cobra.commit();
		}
		else {
    		_timestampLoid = timestampLoid;
    		CommitTimestamp timestamp = timestamp();
    		println("Timestamp read: " + timestamp.value());
    		_timeStampIdGenerator.setMinimumNext(timestamp.value());
		}
	}

	private CommitTimestamp timestamp() {
		return _cobra.objectByLoid(_timestampLoid);
	}

	public static EventClient newEventClient(EventConfiguration config)  {
		try{
			return new EventClient(config.serverHost,config.serverPort,config.clientHost,config.clientPort,config.databaseName);
		} catch (IOException ioException){
			System.err.println("Connection failed using\n" + config + "\nMake sure that " + VodDatabase.VED_DRIVER + " is running.");
			unrecoverableExceptionOccurred(ioException);
		}
		return null;
	}

	public void run() {
	    createMetaChannel(_client);
	    createCommitSyncChannel();
	    println(LISTENING_MESSAGE + _eventConfiguration.databaseName);
	    taskQueueProcessorLoop();
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

	private void taskQueueProcessorLoop() {
		try{
			while(! _stopped){
				try {
					Block4 task = _queuedTasks.next();
					synchronized(_lock) {
						task.run();
					}
				} catch(BlockingQueueStoppedException ex){
					break;
				}
			}
		} catch (Exception ex){
			unrecoverableExceptionOccurred(ex);
		}
	}
	
	private void createChannel(final ClassChannelSpec channelSpec) {
		EventChannel channel = produceClassChannel(channelSpec._className);
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceModified (VersantEventObject event){
				queueObjectLifeCycleEvent(event, Operations.UPDATE, channelSpec);
			}
			public void instanceCreated (VersantEventObject event) {
				queueObjectLifeCycleEvent(event, Operations.CREATE, channelSpec);
			}
			public void instanceDeleted (VersantEventObject event) {
				queueObjectLifeCycleEvent(event, Operations.DELETE, channelSpec);				
			}
		});
		println("Listener channel created for class " + channelSpec._className);
	}

	private void createCommitSyncChannel() {
		EventChannel channel = produceClassChannel(TimestampSyncRequest.class.getName());
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceModified (VersantEventObject event){
				queueSynchronizationRequest(event);
			}
			
			public void instanceCreated (VersantEventObject event) {
				queueSynchronizationRequest(event);
			}
			
			public void instanceDeleted (VersantEventObject event) {
			}
			
			private void queueSynchronizationRequest(VersantEventObject event) {
				_queuedTasks.add(new TimestampSyncRequestTask(event.getRaiserLoid()));
			}
		});
	}

	private EventChannel createMetaChannel(final EventClient client)  {
		EventChannel channel = produceClassChannel(ClassMetadata.class.getName());
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceCreated (VersantEventObject event) {
				RegisterClassMetadataTask task = new RegisterClassMetadataTask(event.getRaiserLoid());
				_queuedTasks.add(task);
			}
			public void instanceModified (VersantEventObject event){
				// do nothing
			}
			public void instanceDeleted (VersantEventObject event) {
				// do nothing
			}
		});
		return channel;
	}

	private EventChannel produceClassChannel(String className) {
		String channelName = channelName(className);
		try {
			EventChannel channel = _client.getChannel (channelName);
			if(channel != null){
				if(DrsDebug.verbose){
					System.out.println("Reusing existing channel " + channelName);
				}
				return channel;
			}
			ClassChannelBuilder builder = new ClassChannelBuilder (className);
			if(DrsDebug.verbose){
				System.out.println("Creating new channel " + channelName);
			}
			return _client.newChannel (channelName, builder);
		} catch (IOException e) {
			unrecoverableExceptionOccurred(e);
		}
		return null;
	}
	
	private void queueObjectLifeCycleEvent(VersantEventObject event, Operations operation, ClassChannelSpec channelSpec) {
		_queuedTasks.add(new ObjectLifeCycleEventStoreTask(channelSpec._classMetadataLoid, event.getRaiserLoid(), operation));
	}

	private void persistObjectLifeCycleEvent(ObjectLifecycleEvent objectLifecycleEvent) {
		_cobra.store(objectLifecycleEvent);
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
			if(DrsDebug.verbose){
				System.out.println(msg);
			}
		}
	}
	
	public void stop(){
		_stopped = true;
		_queuedTasks.stop();
	}

	private static void unrecoverableExceptionOccurred(Throwable t) {
		t.printStackTrace();
		throw new RuntimeException(t);
		
		
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
				println(COMMIT_MESSAGE);
				_dirty = false;
			}
		}
	}
	
	public class ClassChannelSpec {

		public final String _className;
		
		public final String _fullyQualifiedName;
		
		public final long _classMetadataLoid;

		public ClassChannelSpec(String className, String fullyQualifiedName, long classMetadataLoid) {
			_className = className;
			_fullyQualifiedName = fullyQualifiedName;
			_classMetadataLoid = classMetadataLoid;
		}
		
		@Override
		public String toString() {
			return com.db4o.internal.Reflection4.dump(this);
		}

	}

	
	private class ObjectLifeCycleEventStoreTask implements Block4 {

		private long _classLoid;
		private String _objectLoid;
		private Operations _operation;
		
		public ObjectLifeCycleEventStoreTask(long classLoid, String objectLoid, Operations operation) {
			_classLoid = classLoid;
			_objectLoid = objectLoid;
			_operation = operation;
		}
		
		public void run() {
			long loid = VodCobra.loidAsLong(_objectLoid);
			ObjectLifecycleEvent objectLifecycleEvent = 
				new ObjectLifecycleEvent(
						_classLoid,
						loid,
						_operation.value,
						_timeStampIdGenerator.generate());
			persistObjectLifeCycleEvent(objectLifecycleEvent);
			println("Event stored: " + objectLifecycleEvent);
			_dirty = true;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+ ": " + _classLoid + ", " + _objectLoid + ", " + _operation;
		}
	}
	
	private class TimestampSyncRequestTask implements Block4 {
		private String _loidString;
		
		private TimestampSyncRequestTask(String loidString) {
			_loidString = loidString;
		}
		
		public void run() {
			long syncRequestLoid = VodCobra.loidAsLong(_loidString);
			TimestampSyncRequest syncRequest = _cobra.objectByLoid(syncRequestLoid);
			if(syncRequest.isAnswered()) {
				return;
			}
			CommitTimestamp timestamp = timestamp();
			syncRequest.timestamp(timestamp.value());
			_cobra.store(syncRequestLoid, syncRequest);
			_cobra.commit();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+ ": " + _loidString;
		}
	}

	private class RegisterClassMetadataTask implements Block4 {

		private String _classId;
		
		public RegisterClassMetadataTask(String classId) {
			_classId = classId;
		}
		
		public void run() {
			long classMetadataLoid = VodCobra.loidAsLong(_classId);
			ClassMetadata origClassMetadata = _cobra.objectByLoid(classMetadataLoid);
			_dirty = true;
			createChannel(new ClassChannelSpec(origClassMetadata.name(),origClassMetadata.fullyQualifiedName(), classMetadataLoid));
			ClassMetadata flaggedClassMetadata = new ClassMetadata(origClassMetadata.name(),origClassMetadata.fullyQualifiedName(), true);
			_cobra.store(classMetadataLoid, flaggedClassMetadata);
			_cobra.commit();
		}		
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+ ": " + _classId;
		}
	}

}
