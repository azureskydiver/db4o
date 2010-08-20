/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;
import java.util.*;

import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.EventProcessorNetwork.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.*;
import com.db4o.foundation.*;
import com.versant.event.*;

public class EventProcessor {
	
	public static final String LISTENING_MESSAGE = "Listening for events on ";
	
	public static final String COMMIT_MESSAGE = "EventProcessor commit";

	private static final long PAUSE_TIMEOUT = 60000;

	private static final int PAUSE_INTERVAL = 50;

	private final int COMMIT_INTERVAL = 1000; // 1 sec
	
	private static int EVENT_PROCESSOR_ID = EventProcessor.class.getName().hashCode();
	
	private final LinePrinter _out;
	
	private final VodEventClient _client;
	
	private volatile boolean _stopped;

	private final VodCobra _cobra;
	
	private final TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();
	
	private PausableBlockingQueue<Block4> _pausableTasks = new PausableBlockingQueue<Block4>();
	
	private CommunicationChannelControl _incomingMessages;

	private volatile boolean _dirty;
	
	private final Object _lock = new Object();
	
	SimpleTimer _commitTimer = new SimpleTimer(
			new Runnable() {
				public void run() {
					commit();
				}}, 
			COMMIT_INTERVAL);
	
	private Thread _commitThread = new Thread(_commitTimer, "EventProcessor Commit");
	
	private CommitTimestamp _commitTimestamp;

	private Set<String> _knownClasses = new HashSet<String>();


	public EventProcessor(VodEventClient client, LinePrinter out, VodCobra cobra)  {
		
		_out = out;
		_client = client;
	    _cobra = cobra;

	    produceLastTimestamp();
	    startChannelsFromKnownClasses();
	}

	private void startChannelsFromKnownClasses() {
		Collection<Long> classMetadataLoids = _cobra.loids(ClassMetadata.class);
	    for (Long loid : classMetadataLoids) {
	    	ClassMetadata classMetadata = _cobra.objectByLoid(loid);
	    	createChannel(new ClassChannelSpec(classMetadata.name(), classMetadata.fullyQualifiedName(),  loid));
	    	_knownClasses.add(classMetadata.fullyQualifiedName());
		}
	}

	private void produceLastTimestamp() {
		_commitTimestamp = _cobra.singleInstanceOrDefault(CommitTimestamp.class, new CommitTimestamp(0));
		if(_commitTimestamp.value() == 0){
			_cobra.store(_commitTimestamp);
			_cobra.commit();
			println("No CommitTimestamp found. Initializing.");
			return;
			
		} 
		println("Timestamp read: " + _commitTimestamp.value());
		_timeStampIdGenerator.setMinimumNext(_commitTimestamp.value());
	}

	public static EventClient newEventClient(EventConfiguration config)  {
		try{
			return new EventClient(config.serverHost,config.serverPort,config.clientHost,config.clientPort(),config.databaseName);
		} catch (IOException ioException){
			System.err.println("Connection failed using\n" + config + "\nMake sure that " + VodDatabase.VED_DRIVER + " is running.");
			unrecoverableExceptionOccurred(ioException);
		}
		return null;
	}

	public void run() {
	    _commitThread.start();
		_incomingMessages = EventProcessorNetworkFactory.prepareProviderCommunicationChannel(createProvider(), _lock, _cobra, _client, EVENT_PROCESSOR_ID);
		println(LISTENING_MESSAGE + _cobra.databaseName());
		startPausableTasksExecutor();
		_incomingMessages.start();
		try {
			_incomingMessages.join();
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
		shutdown();
	}
	

	private void startPausableTasksExecutor() {

		Thread t = new Thread("Pausable tasks executor") {
			@Override
			public void run() {
				try {
					while(!_stopped) {
						Block4 next = _pausableTasks.next();
						synchronized (_lock) {
							next.run();
						}
					}
				} catch (BlockingQueueStoppedException e){
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

	

	private ProviderSideCommunication createProvider() {
		
		return new ProviderSideCommunication() {
			
			public void syncTimestamp(long newTimeStamp) {
				if(newTimeStamp > 0){
					_timeStampIdGenerator.setMinimumNext(newTimeStamp);
				}
			}
			
			public long requestTimestamp() {
				return _timeStampIdGenerator.last();
			}
			
			public void requestIsolation(boolean isolated) {
				
				if(_pausableTasks.isPaused() == isolated) {
					return;
				}
		
				// FIXME: timeout for isolation mode (can rely on new implementation of BlockingQueue#next(long timeout)
				
				if (isolated) {
					_pausableTasks.pause();
				} else {
					_pausableTasks.resume();
				}
			}
		
			public void ensureMonitoringEventsOn(String fullyQualifiedName, String schemaName, long classLoid) {
				if (_knownClasses.contains(fullyQualifiedName)) {
					return;
				}
				createChannel(new ClassChannelSpec(schemaName,fullyQualifiedName, classLoid));
				_knownClasses.add(fullyQualifiedName);
				dirty();
			}
	
		};
	}


	private void shutdown() {
		_client.shutdown();
		_commitTimer.stop();
		try {
			_commitThread.join();
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
		commit();
		_cobra.close();
	}

	private void createChannel(final ClassChannelSpec channelSpec) {
		EventChannel channel = _client.produceClassChannel(channelSpec._className);
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

	private void queueObjectLifeCycleEvent(VersantEventObject event, Operations operation, ClassChannelSpec channelSpec) {
		_pausableTasks.add(new ObjectLifeCycleEventStoreTask(channelSpec._classMetadataLoid, event.getRaiserLoid(), operation));
	}

	private void persistObjectLifeCycleEvent(ObjectLifecycleEvent objectLifecycleEvent) {
		_cobra.store(objectLifecycleEvent);
	}

	private synchronized void println(String msg) {
		synchronized (_lock) {
			_out.println(msg);
			if(DrsDebug.verbose){
				System.out.println(msg);
			}
		}
	}
	
	public void stop(){
		_stopped = true;
		_pausableTasks.stop();
		_incomingMessages.stop();
		try {
			_incomingMessages.join();
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}

	public static void unrecoverableExceptionOccurred(Throwable t) {
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
				_commitTimestamp.value(_timeStampIdGenerator.last());
				_cobra.store(_commitTimestamp);
				_cobra.commit();
				println(COMMIT_MESSAGE);
				_dirty = false;
			}
		}
	}
	
	private void dirty() {
		_dirty = true;
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
			dirty();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+ ": " + _classLoid + ", " + _objectLoid + ", " + _operation;
		}
	}

}
