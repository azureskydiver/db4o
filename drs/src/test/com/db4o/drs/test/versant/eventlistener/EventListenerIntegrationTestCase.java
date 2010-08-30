/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant.eventlistener;

import java.util.*;

import com.db4o.drs.test.versant.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.*;
import com.db4o.foundation.*;

import db4ounit.*;


public class EventListenerIntegrationTestCase extends VodEventTestCaseBase {
	
	public void testStoreSingleObject() throws Exception {
		withEventProcessor(new Closure4<Void>() {
			public Void run() {
				final Item item = storeAndCommitItem();
				Assert.isTrue(checkObjectLifeCycleEventFor(item, 3000), "Timeout: ObjectLifecycleEvent object not stored.");
				return null;
			}
		});
	}

	public void testStoreSingleObjectDuringIsolation() throws Exception {
		withEventProcessor(new Closure4<Void>() {
			public Void run() {
				final ByRef<Item> item = ByRef.<Item>newInstance();
				_provider.runIsolated(new Block4() {
					public void run() {
						item.value = storeAndCommitItem();
						Assert.isFalse(checkObjectLifeCycleEventFor(item.value, 3000), "ObjectLifecycleEvent stored during isolation.");
					}
				});
				Assert.isTrue(checkObjectLifeCycleEventFor(item.value, 3000), "Timeout: ObjectLifecycleEvent object not stored.");
				return null;
			}
		});
	}

	public void testStartingEventProcessorTwice() throws Exception {
		for (int i = 0; i < 2; i++) {
			withEventProcessor(new Closure4<Void>() {
				public Void run() {
					storeAndCommitItem();
					return null;
				}
			}, "Listening");
		}
	}
	
	public void testEventProcessorReloadsClasses() throws Exception {
		for (int i = 0; i < 2; i++) {
			withEventProcessor(new Closure4<Void>() {
				public Void run() {
					storeAndCommitItem();
					return null;
				}
			}, "Item");
		}
	}
	
	public void testEventProcessor10Times() throws Exception {
		for (int i = 0; i < 10; i++) {
			withEventProcessor(new Closure4<Void>() {
				public Void run() {
					storeAndCommitItem();
					return null;
				}
			}, "Event stored");
		}
	}
	
	public void testPersistentTimestampExistsAfterEvent() throws Exception {
		withEventProcessor(new Closure4<Void>() {
			public Void run() {
				storeAndCommitItem();
				return null;
			}
		}, "Item");
		Collection<CommitTimestamp> timestamps = _jdo.query(CommitTimestamp.class);
		Assert.areEqual(1, timestamps.size());
	}
	
	private Item storeAndCommitItem() {
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		return item;
	}

	private boolean checkObjectLifeCycleEventFor(final Item item,
			long timeout) {
		boolean result = Runtime4.retry(timeout, new Closure4<Boolean>() {
			public Boolean run() {
				final long objectLoid = _provider.loid(item);
				Collection<ObjectLifecycleEvent> objectLifecycleEvents = 
					_jdo.query(ObjectLifecycleEvent.class, "this.objectLoid == " + objectLoid); 
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
		return result;
	}
	
	public void testIsolationTimeout() {
		
		final ObjectLifecycleMonitor original = _provider.eventProcessor();
		
		_provider.eventProcessor(new ObjectLifecycleMonitor() {

			public boolean requestIsolation(boolean isolated) {
				return original.requestIsolation(isolated);
			}

			public long requestTimestamp() {
				return original.requestTimestamp();
			}

			public void syncTimestamp(long timestamp) {
				original.syncTimestamp(timestamp);
			}

			public void ensureMonitoringEventsOn(String fullyQualifiedName, String schemaName, long classLoid) {
				original.ensureMonitoringEventsOn(fullyQualifiedName, schemaName, classLoid);
			}

			public void ping() {
				// ignoring ping
			}

			public void stop() {
				original.stop();
			}
			
		});
		
		try {
		
			ObjectLifecycleMonitor ep = _vod.startEventProcessor().eventProcessor();
			
			Assert.isTrue(checkObjectLifeCycleEventFor(storeAndCommitItem(), 1000));
			
			Assert.isTrue(ep.requestIsolation(true));
	
			long timeout = ObjectLifecycleMonitorImpl.ISOLATION_TIMEOUT;
			Assert.isFalse(checkObjectLifeCycleEventFor(storeAndCommitItem(), timeout/2));
			
			Runtime4.sleepThrowsOnInterrupt(ObjectLifecycleMonitorImpl.ISOLATION_TIMEOUT);
			Assert.isTrue(checkObjectLifeCycleEventFor(storeAndCommitItem(), timeout/2));
			
			Assert.isTrue(ep.requestIsolation(true));
			Assert.isTrue(ep.requestIsolation(false));
			
			ep.stop();
			
		} finally {
			_provider.eventProcessor(original);
		}
	}

}
