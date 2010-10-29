/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.eventlistener;

import static com.db4o.qlin.QLinSupport.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.db4o.drs.inside.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.tcp.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectInfo.Operations;
import com.db4o.drs.versant.metadata.ClassMetadata;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.versant.event.*;

public class EventProcessorImpl implements Runnable, EventProcessor {
	
	public static final String SIMPLE_NAME = ReflectPlatform.simpleName(EventProcessor.class);

	public static final long ISOLATION_TIMEOUT = 5000;

	private static final long ISOLATION_WATCHDOG_INTERVAL = 1000;

	private final VodEventClient _client;
	
	private volatile boolean _stopped;

	private final VodCobraFacade _cobra;
	
	private final TimeStampIdGenerator _timeStampIdGenerator = new TimeStampIdGenerator();
	
	private TimeoutBlockingQueue4<Block4> _pausableTasks = new TimeoutBlockingQueue<Block4>(ISOLATION_TIMEOUT);
	
	private ServerChannelControl _incomingMessages;

	private final Object _lock = new Object();
	
	private final Map<String, List<ObjectInfo>> _objectInfos = new HashMap<String, List<ObjectInfo>>();
	
	private Map<Long, Long> _loidTimeStamps = new HashMap<Long, Long>();
	
	private long _defaultSignatureLoid;
	
	
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

	private Map<String, Long> _knownClasses = new HashMap<String, Long>();

	private List<EventProcessorListener> _listeners = new ArrayList<EventProcessorListener>();

	private boolean _started;

	private final VodDatabase _vod;

	public EventProcessorImpl(VodEventClient client, VodDatabase vod)  {
		_client = client;
		this._vod = vod;
	    _cobra = VodCobra.createInstance(vod);
	    produceLastTimestamp();
	    startChannelsFromKnownClasses();
	    defaultSignatureLoid();
	}


	private void startChannelsFromKnownClasses() {
		Collection<Long> classMetadataLoids = _cobra.loids(ClassMetadata.class);
	    for (Long loid : classMetadataLoids) {
	    	ClassMetadata classMetadata = _cobra.objectByLoid(loid);
	    	createChannel(new ClassChannelSpec(classMetadata.name(), classMetadata.fullyQualifiedName(),  loid), false);
	    	_knownClasses.put(classMetadata.fullyQualifiedName(), classMetadata.loid());
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
		_isolatinWatchdogThread.setDaemon(true);
	    _isolatinWatchdogThread.start();
		_incomingMessages = TcpCommunicationNetwork.prepareCommunicationChannel(this, _vod, _client);
		startPausableTasksExecutor();
		synchronized (_listeners) {
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
				runPausableTasks();
			}

		};
		t.setDaemon(true);
		t.start();
	}
	
	private void runPausableTasks() {
		try {
			Collection4<Block4> list = new Collection4<Block4>();
			while(!_stopped) {
				_pausableTasks.drainTo(list);
				synchronized (_lock) {
					if(_stopped){
						return;
					}
					Iterator4<Block4> it = list.iterator();
					while(it.moveNext()) {
						it.current().run();
					}
					list.clear();
				}
			}
		} catch (BlockingQueueStoppedException e){
		}
	}
	
	public void syncTimestamp(long newTimeStamp) {
		if(newTimeStamp > 0){
			_timeStampIdGenerator.setMinimumNext(newTimeStamp);
		}
	}
	
	public long lastTimestamp() {
		long timestamp = _timeStampIdGenerator.last();
		if(timestamp != 0){
			return timestamp;
		}
		return generateTimestamp();
	}
	
	public long generateTimestamp() {
		_timeStampIdGenerator.generate();
		return lastTimestamp();
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
	
	public void ping() {
		if (!_pausableTasks.isPaused()) {
			return;
		}
		_pausableTasks.reset();
	}
	
	private void shutdown() {
		_client.shutdown();
		_isolationWatchdogTimer.stop();
		try {
			_isolatinWatchdogThread.join();
		} catch (InterruptedException e) {
		}
		synchronized (_lock) {
			_cobra.close();
		}
	}

	private void createChannel(final ClassChannelSpec channelSpec, boolean registerTransactionEvents) {
		EventChannel channel = _client.produceClassChannel(channelSpec._className, registerTransactionEvents);
		if(! channel.getListeners().isEmpty()){
			println("Listener already exists for " + channelSpec._className);
			return;
		}
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
		
		channel.addVersantEventListener (new TransactionMarkerEventListener() {
			public void endTransaction(final VersantEventObject event) {
				_pausableTasks.add(new TransactionCommitTask(event));
				
			}
			public void beginTransaction(VersantEventObject event) {
				// do nothing
			}
		});
		
		println("Listener channel created for class " + channelSpec._className);
	}
	
	private void queueObjectLifeCycleEvent(VersantEventObject event, Operations operation, ClassChannelSpec channelSpec) {
		_pausableTasks.add(new ObjectLifeCycleEventStoreTask(event.getTransactionID(), channelSpec._classMetadataLoid, event.getRaiserLoid(), operation));
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
	
	private void commit(String transactionId) {
		synchronized (_lock) {
			List<ObjectInfo> infos = _objectInfos.remove(transactionId);
			if(infos != null){
				for(ObjectInfo info: infos){
					long objectLoid = info.objectLoid();
					ObjectInfo objectInfo = prototype(ObjectInfo.class);
					ObjectInfo infoToStore = _cobra
						.from(ObjectInfo.class)
						.where(objectInfo.objectLoid())
						.equal(objectLoid)
						.singleOrDefault(info);
					if(infoToStore != info){
						infoToStore.copyStateFrom(info);
					}
					Long timestamp = _loidTimeStamps.remove(objectLoid);
					if(timestamp != null){
						infoToStore.version(timestamp);
					}
					info.version(infoToStore.version());
					_cobra.store(infoToStore);
					println("stored: " + infoToStore);
				}
			}
			_commitTimestamp.value(lastTimestamp());
			_cobra.store(_commitTimestamp);
			_cobra.commit();
			
			if(infos != null){
				for(ObjectInfo info: infos){
					long objectLoid = info.objectLoid();
					long version = info.version();
					listenerTrigger().onEvent(objectLoid, version);
				}
			}
			
			listenerTrigger().committed(transactionId);
			println(SIMPLE_NAME+" commit");
		}
	}

	private final class TransactionCommitTask implements Block4 {
		private final VersantEventObject _event;

		private TransactionCommitTask(VersantEventObject event) {
			_event = event;
		}

		public void run() {
			commit(_event.getTransactionID());
		}

		@Override
		public String toString() {
			return "pausable commit";
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

		private final String _transactionId;
		private final long _classLoid;
		private final String _objectLoid;
		private final Operations _operation;
		private final long _timeStamp;
		
		public ObjectLifeCycleEventStoreTask(String transactionId, long classLoid, String objectLoid, Operations operation) {
			_transactionId = transactionId;
			_classLoid = classLoid;
			_objectLoid = objectLoid;
			_operation = operation;
			generateTimestamp();
			_timeStamp = lastTimestamp();
		}
		
		public void run() {
			
			long loid = VodCobra.loidAsLong(_objectLoid);
			
			ObjectInfo objectInfo = 
				new ObjectInfo(
						defaultSignatureLoid(),
						_classLoid,
						loid,
						_timeStamp,
						_timeStamp,
						_operation.value);
			List<ObjectInfo> infos = _objectInfos.get(_transactionId);
			if(infos == null){
				infos = new java.util.LinkedList<ObjectInfo>();
				_objectInfos.put(_transactionId, infos);
			}
			infos.add(objectInfo);
			println("Event registered: " + objectInfo);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName()+ ": " + _classLoid + ", " + _objectLoid + ", " + _operation;
		}
	}
	
	public long defaultSignatureLoid() {
		if (_defaultSignatureLoid > 0) {
			return _defaultSignatureLoid;
		}
		_defaultSignatureLoid = _cobra.queryForMySignatureLoid();
		if (_defaultSignatureLoid != 0) {
			return _defaultSignatureLoid;
		}
		DatabaseSignature databaseSignature = 
			new DatabaseSignature(_cobra.databaseId(), _cobra.signatureBytes(_cobra.databaseId()));
		_cobra.store(databaseSignature);
		_cobra.commit();
		_defaultSignatureLoid = databaseSignature.loid();
		return _defaultSignatureLoid;
	}

	public void addListener(EventProcessorListener l) {
		synchronized (_listeners) {
			_listeners.add(l);
			if (_started) {
				l.ready();
			}
		}
	}
	
	public void removeListener(EventProcessorListener listener) {
		synchronized (_listeners) {
			_listeners.remove(listener);
		}
	}
	
	private EventProcessorListener listenerTrigger() {
		return (EventProcessorListener) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{EventProcessorListener.class}, new InvocationHandler() {
			
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				ArrayList<EventProcessorListener> ls;
				synchronized (_listeners) {
					ls = new ArrayList<EventProcessorListener>(_listeners);
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

	public Map<String, Long> ensureMonitoringEventsOn(final String className) {

		Map<String, Long> classIds = new HashMap<String, Long>();

		synchronized (_lock) {
			if(className == null){
				return classIds;
			}
			
			Long classMetadataLoid = _knownClasses.get(className);
			if (classMetadataLoid == null) {
				classMetadataLoid = produceChannel(className);
				_knownClasses.put(className, classMetadataLoid);
				classIds.put(className, classMetadataLoid);
			} else {
				classIds.put(className, classMetadataLoid);
			}
		}

		return classIds;
	}

	private long produceChannel(String fullyQualifiedName) {
		
		Long cmLoid = _knownClasses.get(fullyQualifiedName);
		
		if (cmLoid != null) {
			return cmLoid;
		}
		
		String schemaName = schemaFor(fullyQualifiedName);
		
		ClassMetadata cm = ensureClassMetadata(fullyQualifiedName, schemaName);

		createChannel(new ClassChannelSpec(schemaName, fullyQualifiedName, cm.loid()), false);
		
		_knownClasses.put(fullyQualifiedName, cm.loid());
		
		return cm.loid();
	}

	private ClassMetadata ensureClassMetadata(String fullyQualifiedName, String schemaName) {
		ClassMetadata classMetadata = prototype(ClassMetadata.class);
		ClassMetadata storedClassMetadata = _cobra.from(ClassMetadata.class).where(classMetadata.name()).equal(schemaName).singleOrDefault(null);
		if(storedClassMetadata != null){
			return storedClassMetadata;
		}
		classMetadata = new ClassMetadata(schemaName, fullyQualifiedName);
		_cobra.store(classMetadata);
		return classMetadata;
	}

	private String schemaFor(String fullyQualifiedName) {
		try {
			return _cobra.schemaName(Class.forName(fullyQualifiedName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public void forceTimestamps(List<Pair<Long, Long>> loidTimeStamps) {
		synchronized (_lock) {
			for (Pair<Long, Long> pair : loidTimeStamps) {
				_loidTimeStamps.put(pair.first, pair.second);
			}
		}
	}

}
