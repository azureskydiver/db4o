/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.EventProcessorNetwork.ServerChannelControl;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.Operations;
import com.db4o.drs.versant.metadata.ClassMetadata;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.versant.event.*;

public class EventProcessorImpl implements Runnable, EventProcessor {
	
	public static final String SIMPLE_NAME = ReflectPlatform.simpleName(EventProcessor.class);
	
	public static final String COMMIT_MESSAGE = SIMPLE_NAME+" commit";

	public static final long ISOLATION_TIMEOUT = 5000;

	private static final long ISOLATION_WATCHDOG_INTERVAL = 1000;

	private final long COMMIT_INTERVAL = 1000; // 1 sec
	
	private final VodEventClient _client;
	
	private volatile boolean _stopped;

	private final VodCobraFacade _cobra;
	
	private final TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();
	
	private TimeoutBlockingQueue4<Block4> _pausableTasks = new TimeoutBlockingQueue<Block4>(ISOLATION_TIMEOUT);
	
	private ServerChannelControl _incomingMessages;

	private volatile boolean _dirty;
	
	private final Object _lock = new Object();
	
	SimpleTimer _commitTimer = new SimpleTimer(
			new Runnable() {
				public void run() {
					commit();
				}}, 
			COMMIT_INTERVAL);
	
	private Thread _commitThread = new Thread(_commitTimer, SIMPLE_NAME+" Commit");
	
	SimpleTimer _isolationWatchdogTimer = new SimpleTimer(
		new Runnable() {
			public void run() {
				if (_pausableTasks.isPaused()) {
					_pausableTasks.check();
				}
			}}, 
		ISOLATION_WATCHDOG_INTERVAL);

	private Thread _isolatinWatchdogThread = new Thread(_isolationWatchdogTimer, SIMPLE_NAME+" Isolation watchdog");

	
	private CommitTimestamp _commitTimestamp;

	private Set<String> _knownClasses = new HashSet<String>();

	private List<EventProcessorListener> listeners = new ArrayList<EventProcessorListener>();

	private boolean _started;

	private BlockingQueue4<Object> events = new BlockingQueue<Object>();

	public AtomicInteger eventStoreCount = new AtomicInteger();

	private final VodDatabase _vod;

	public EventProcessorImpl(VodEventClient client, VodDatabase vod)  {
		
		_client = client;
		this._vod = vod;
	    _cobra = VodCobra.createInstance(vod);

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
		IOException e = null;
		for(int i=0;i<10;i++) {
			try{
				return new EventClient(config.serverHost,config.serverPort,config.clientHost,config.clientPort(),config.databaseName);
			} catch (IOException ioException){
				e = ioException;
				Runtime4.sleepThrowsOnInterrupt(100);
			}
		}
		System.err.println("Connection failed using\n" + config + "\nMake sure that " + VodDatabase.VED_DRIVER + " is running.");
		unrecoverableExceptionOccurred(e);
		return null;
	}

	public void run() {
		_commitThread.setDaemon(true);
		_isolatinWatchdogThread.setDaemon(true);
	    _commitThread.start();
	    _isolatinWatchdogThread.start();
		_incomingMessages = EventProcessorNetworkFactory.prepareProviderCommunicationChannel(this, _vod, _client);
		startPausableTasksExecutor();
		synchronized (listeners) {
			_started = true;
			listenerTrigger().ready();
		}
		try {
			_incomingMessages.join();
		} catch (InterruptedException e) {
		}
		shutdown();
	}
	

	private void startPausableTasksExecutor() {

		Thread t = new Thread("Pausable tasks executor") {
			@Override
			public void run() {
				try {
					Collection4<Block4> list = new Collection4<Block4>();
					while(!_stopped) {
						_pausableTasks.drainTo(list);
						synchronized (_lock) {
							Iterator4<Block4> it = list.iterator();
							while(it.moveNext()) {
								it.current().run();
							}
							list.clear();
							if (_dirty) {
								commit();
							}
						}
					}
				} catch (BlockingQueueStoppedException e){
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}

	public void syncTimestamp(long newTimeStamp) {
		if(newTimeStamp > 0){
			_timeStampIdGenerator.setMinimumNext(newTimeStamp);
		}
	}
	
	public long requestTimestamp() {
		return _timeStampIdGenerator.last();
	}
	
	public boolean requestIsolation(boolean isolated) {
		
		if(_pausableTasks.isPaused() == isolated) {
			return false;
		}
		
		// FIXME: timeout for isolation mode (can rely on new implementation of BlockingQueue#next(long timeout)
		
		if (isolated) {
			_pausableTasks.pause();
		} else {
			_pausableTasks.resume();
		}
		
		return true;
	}
	
	public void ensureMonitoringEventsOn(String fullyQualifiedName, String schemaName, long classLoid) {
		if (_knownClasses.contains(fullyQualifiedName)) {
			return;
		}
		createChannel(new ClassChannelSpec(schemaName,fullyQualifiedName, classLoid));
		_knownClasses.add(fullyQualifiedName);
		dirty();
	}
	
	public void ping() {
		if (!_pausableTasks.isPaused()) {
			return;
		}
		_pausableTasks.reset();
	}
	
	private void shutdown() {
		_client.shutdown();
		_commitTimer.stop();
		try {
			_commitThread.join();
		} catch (InterruptedException e) {
		}
		_isolationWatchdogTimer.stop();
		try {
			_isolatinWatchdogThread.join();
		} catch (InterruptedException e) {
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

	private void println(String msg) {
		if(DrsDebug.verbose){
			System.out.println(msg);
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
		synchronized (_lock) {
			if (_dirty) {
				_commitTimestamp.value(_timeStampIdGenerator.last());
				_cobra.store(_commitTimestamp);
				_cobra.commit();
				int i = eventStoreCount.getAndSet(0);
				while (i-- > 0) {
					events.add(new Object());
				}
				listenerTrigger().commited();
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
			eventStoreCount.getAndIncrement();
			dirty();
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName()+ ": " + _classLoid + ", " + _objectLoid + ", " + _operation;
		}
	}


	public void addListener(EventProcessorListener l) {
		synchronized (listeners) {
			listeners.add(l);
			if (_started) {
				l.ready();
			}
		}
	}
	
	private EventProcessorListener listenerTrigger() {
		return (EventProcessorListener) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{EventProcessorListener.class}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				ArrayList<EventProcessorListener> ls;
				synchronized (listeners) {
					ls = new ArrayList<EventProcessorListener>(listeners);
				}
				for(EventProcessorListener l : ls) {
					try {
						method.invoke(l, args);
					} catch (InvocationTargetException e) {
						throw e.getCause();
					}
				}
				return null;
			}
		});
	}

	public void ensureChangecount(int expectedChangeCount) {
		for(int i=0;i<expectedChangeCount;i++) {
			events.next();
		}
	}

}
