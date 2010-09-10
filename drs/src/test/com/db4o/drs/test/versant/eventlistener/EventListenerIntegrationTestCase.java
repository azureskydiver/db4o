/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant.eventlistener;

import java.util.*;

import com.db4o.drs.test.versant.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitor.MonitorListener;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.Operations;
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

	// do theses tests make sense at all?
//	public void testStartingEventProcessorTwice() throws Exception {
//		for (int i = 0; i < 2; i++) {
//			withEventProcessor(new Closure4<Void>() {
//				public Void run() {
//					storeAndCommitItem();
//					return null;
//				}
//			}, "Listening");
//		}
//	}
//	
//	public void testEventProcessorReloadsClasses() throws Exception {
//		for (int i = 0; i < 2; i++) {
//			withEventProcessor(new Closure4<Void>() {
//				public Void run() {
//					storeAndCommitItem();
//					return null;
//				}
//			}, "Item");
//		}
//	}
	
	public void testEventProcessor10Times() throws Exception {
		for (int i = 0; i < 10; i++) {
			withEventProcessor(new Closure4<Void>() {
				public Void run() {
					storeAndCommitItem();
					return null;
				}
			});
		}
	}
	
	public void testPersistentTimestampExistsAfterEvent() throws Exception {
		withEventProcessor(new Closure4<Void>() {
			public Void run() {
				final BlockingQueue4<Object> q = new BlockingQueue<Object>();
				_provider.syncEventProcessor().addListener(new MonitorListener() {
					
					public void ready() {
					}
					
					public void commited() {
						q.add(new Object());
					}
				});
				storeAndCommitItem();
				q.next();
				return null;
			}
		});
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
	
	public void testIsolationTimeout() throws Exception {
		
		withEventProcessor(new Closure4<Void>() {
			
			public Void run() {
		
				boolean oldState = _provider.pinging();
				_provider.pinging(false);
				
				try {
		
					final ObjectLifecycleMonitor ep = _provider.syncEventProcessor();
					
					Assert.isTrue(checkObjectLifeCycleEventFor(storeAndCommitItem(), 1000));
					
					
					_provider.runIsolated(new Block4() {
						
						public void run() {
							long timeout = ObjectLifecycleMonitorImpl.ISOLATION_TIMEOUT;
							Assert.isFalse(checkObjectLifeCycleEventFor(storeAndCommitItem(), timeout/2));
							
							Runtime4.sleepThrowsOnInterrupt(ObjectLifecycleMonitorImpl.ISOLATION_TIMEOUT);
							Assert.isTrue(checkObjectLifeCycleEventFor(storeAndCommitItem(), timeout/2));
							
							Assert.isTrue(ep.requestIsolation(true));
							Assert.isTrue(ep.requestIsolation(false));
						}
					});
					
				} finally {
					_provider.pinging(oldState);
				}
				return null;
			}
		});
	}

	@Override
	protected Class[] persistedClasses() {
		return new Class[] {Item.class};
	}

}
