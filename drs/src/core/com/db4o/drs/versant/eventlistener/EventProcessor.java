/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;
import java.util.*;

import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
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
	
	private final LinePrinter _out;
	
	private final VodEventClient _client;
	
	private volatile boolean _stopped;

	private volatile long _pausedAt;
	
	private final VodCobra _cobra;
	
	private final TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();
	
	private final BlockingQueue<Block4> _queuedTasks = new BlockingQueue<Block4>();
	private Block4 _pendingTask = null;
	
	private volatile boolean _dirty;
	
	private final Object _lock;
	
	SimpleTimer _commitTimer = new SimpleTimer(
			new Runnable() {
				public void run() {
					commit();
				}}, 
			COMMIT_INTERVAL, 
			"EventProcessor Commit");
	
	private Thread _commitThread = new Thread(_commitTimer);
	
	private CommitTimestamp _commitTimestamp;
	
	private EventProcessorSideCommunication _comm;
	
	public EventProcessor(VodEventClient client, LinePrinter out, VodCobra cobra, EventProcessorSideCommunication comm, Object lock)  {
		
		_lock = lock;
		_out = out;
		_comm = comm;
		_client = client;
	    _cobra = cobra;
	    
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
	    registerClassMetadataListener();
	    registerSyncRequestListener();
	    registerIsolationRequestListener();
	    println(LISTENING_MESSAGE + _cobra.databaseName());
	    taskQueueProcessorLoop();
		shutdown();
	}

	private void shutdown() {
		_client.shutdown();
		_comm.shutdown();
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
				if(isActive()) {
					if(!processQueuedTask()) {
						break;
					}
				}
				else {
					pauseQueue();
				}
			}
		} catch (Exception ex){
			unrecoverableExceptionOccurred(ex);
		}
	}

	private boolean processQueuedTask() {
		synchronized(_lock) {
			if(isActive() && _pendingTask != null) {
				logIsolation("UNSTASHING " + _pendingTask);
				_pendingTask.run();
				logIsolation("PROCESSED " + _pendingTask);
				_pendingTask = null;
				return true;
			}
		}
		try {
			Block4 task = _queuedTasks.next();
			logIsolation("UNQUEUED " + task);
			synchronized(_lock) {
				if(!isActive()) {
					_pendingTask = task;
					logIsolation("STASHED " + task);
					return true;
				}
				task.run();
			}
			logIsolation("PROCESSED " + task);
			return true;
		} catch(BlockingQueueStoppedException ex){
			return false;
		}
	}
	
	private void pauseQueue() {
		logIsolation("PAUSE");
		boolean unpaused = Runtime4.retry(PAUSE_TIMEOUT, PAUSE_INTERVAL, new Closure4<Boolean>() {
			public Boolean run() {
				return isActive();
			}
		});
		logIsolation("UNPAUSE");
		if(!unpaused) {
			throw new IllegalStateException("Isolated state timed out.");
		}
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

	private void registerSyncRequestListener() {
		_comm.registerSyncRequestListener(new Procedure4<Long>() {
			public void apply(Long newTimeStamp) {
				synchronized (_lock) {
					if(newTimeStamp > 0){
						_timeStampIdGenerator.setMinimumNext(newTimeStamp);
					}
					_comm.sendTimestamp(_timeStampIdGenerator.last());
				}
			}
		});
	}

	private void registerIsolationRequestListener() {
		_comm.registerIsolationRequestListener(new Procedure4<Integer>() {
			public void apply(Integer isolationMode) {
				synchronized (_lock) {
					if(isActive() == (isolationMode == IsolationMode.IMMEDIATE)) {
						return;
					}
					switch(isolationMode) {
						case IsolationMode.DELAYED:
							_pausedAt = System.currentTimeMillis();
							break;
						case IsolationMode.IMMEDIATE:
							_pausedAt = 0;
					}
					_comm.acknowledgeIsolationMode(isolationMode);
				}
			}
		});
	}

	private boolean isActive() {
		return _pausedAt == 0;
	}
	
	private void registerClassMetadataListener()  {
		EventChannel channel = _client.produceClassChannel(ClassMetadata.class.getName());
		channel.addVersantEventListener (new ClassEventListener() {
			public void instanceCreated (VersantEventObject event) {
				_queuedTasks.add(new RegisterClassMetadataTask(event.getRaiserLoid()));
			}
			public void instanceModified (VersantEventObject event){
				// do nothing
			}
			public void instanceDeleted (VersantEventObject event) {
				// do nothing
			}
		});
	}

	private void queueObjectLifeCycleEvent(VersantEventObject event, Operations operation, ClassChannelSpec channelSpec) {
		ObjectLifeCycleEventStoreTask task = new ObjectLifeCycleEventStoreTask(channelSpec._classMetadataLoid, event.getRaiserLoid(), operation);
		_queuedTasks.add(task);
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
		_queuedTasks.stop();
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

	private class RegisterClassMetadataTask implements Block4 {

		private String _classId;
		
		public RegisterClassMetadataTask(String classId) {
			_classId = classId;
		}
		
		public void run() {
			long classMetadataLoid = VodCobra.loidAsLong(_classId);
			ClassMetadata classMetadata = _cobra.objectByLoid(classMetadataLoid);
			_dirty = true;
			createChannel(new ClassChannelSpec(classMetadata.name(),classMetadata.fullyQualifiedName(), classMetadataLoid));
			_comm.acknowledgeClassMetadataRegistration(classMetadata.fullyQualifiedName());
		}		
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+ ": " + _classId;
		}
	}

	private static void logIsolation(String msg) {
		System.err.println("*** ISO - " + msg);
	}
}
