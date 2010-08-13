/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant.eventlistener;

import java.util.*;

import com.db4o.drs.test.versant.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.*;
import com.db4o.foundation.*;

import db4ounit.*;


public class EventListenerIntegrationTestCase extends VodEventTestCaseBase {
	
	public void testStoreSingleObject() throws Exception {
		withEventProcessor(new Closure4<Void>() {
			public Void run() {
				final Item item = storeAndCommitItem();
				Assert.isTrue(checkObjectLifeCycleEventFor(item, 10000), "Timeout: ObjectLifecycleEvent object not stored.");
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
						Assert.isFalse(checkObjectLifeCycleEventFor(item.value, 10000), "ObjectLifecycleEvent stored during isolation.");
					}
				});
				Assert.isTrue(checkObjectLifeCycleEventFor(item.value, 10000), "Timeout: ObjectLifecycleEvent object not stored.");
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
			int timeout) {
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

}
