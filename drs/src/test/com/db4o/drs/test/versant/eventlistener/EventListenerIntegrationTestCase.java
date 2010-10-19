/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant.eventlistener;

import java.util.*;

import com.db4o.*;
import com.db4o.drs.test.versant.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.eventlistener.*;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.ipc.EventProcessor.EventProcessorListener;
import com.db4o.drs.versant.metadata.*;
import com.db4o.drs.versant.metadata.ObjectLifecycleEvent.Operations;
import com.db4o.foundation.*;

import static com.db4o.qlin.QLinSupport.*;

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
	
	public void testStoreTwoObjects() throws Exception {
		withEventProcessor(new Closure4<Void>() {
			public Void run() {
				
				Item itemOne = new Item("one");
				Item itemTwo = new Item("two");
				_provider.storeNew(itemOne);
				_provider.storeNew(itemTwo);
				
				_provider.commit();
				
				// TODO: check we only have one commit in the event processor

				final Item item = storeAndCommitItem();
				Assert.isTrue(checkObjectLifeCycleEventFor(item, 3000), "Timeout: ObjectLifecycleEvent object not stored.");
				return null;
			}
		});
	}


	public void testStoreSingleObjectDuringIsolation() throws Exception {
		withEventProcessor(new Closure4<Void>() {
			public Void run() {
				
				
				final BlockingQueue<String> transactionQueue = new BlockingQueue<String>();
				_provider.syncEventProcessor().addListener(new EventProcessorListener() {
					
					public void ready() {
					}
					
					public void committed(String transactionId) {
						synchronized(transactionQueue){
							System.err.println("transactionId in committed " +  transactionId);
							transactionQueue.add(transactionId);
						}
					}

					public void onEvent(long loid) {
						
					}
				});

				// make sure event listener is on for the Item class.
				storeAndCommitItem();
				
				final ByRef<Pair<VodJdoFacade,Item>> pair = ByRef.<Pair<VodJdoFacade,Item>>newInstance();
				
				_provider.runIsolated(new Block4() {
					public void run() {
						pair.value = storeAndCommitItemByOtherUser();
						Assert.isFalse(checkObjectLifeCycleEventFor(pair.value.first, pair.value.second, 3000), "ObjectLifecycleEvent stored during isolation.");
					}
				});
				
				
//				String providerTransactionId = _provider.transactionId();
//				System.err.println("providerTransactionId " + providerTransactionId);
//				_provider.storeNew(new Item("two"));
//				_provider.commit();
//				System.err.println("new providerTransactionId " + _provider.transactionId());
//				synchronized(transactionQueue){
//					while(true){
//						String currentTransactionId = transactionQueue.next();
//						System.err.println("currentTransactionId " + currentTransactionId);
//						if(currentTransactionId.equals(providerTransactionId)){
//							break;
//						}
//						try {
//							transactionQueue.wait(10);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//				}
				
				// _provider.ensureAllEventsStored(transactionQueue.next());
				
				transactionQueue.next();
				
				// Runtime4.sleep(10000);
				Assert.isTrue(checkObjectLifeCycleEventFor(pair.value.first, pair.value.second, 3000), "Timeout: ObjectLifecycleEvent object not stored.");
				pair.value.first.close();

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
			});
		}
	}
	
	public void testEventProcessorReloadsClasses() throws Exception {
		for (int i = 0; i < 2; i++) {
			withEventProcessor(new Closure4<Void>() {
				public Void run() {
					storeAndCommitItem();
					return null;
				}
			});
		}
	}
	
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
				final BlockingQueue4<Long> q = new BlockingQueue<Long>();
				_provider.syncEventProcessor().addListener(new EventProcessorListener() {
					
					public void ready() {
					}
					
					public void committed(String transactionId) {
						
					}

					public void onEvent(long loid) {
						q.add(loid);
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
	
	private Pair storeAndCommitItemByOtherUser() {
		VodJdoFacade jdo = VodJdo.createInstance(_vod);
		Item item = new Item("one");
		jdo.store(item);
		jdo.commit();
		return new Pair(jdo, item);
	}
	
	private boolean checkObjectLifeCycleEventFor(final Item item,long timeout) {
		return checkObjectLifeCycleEventFor(_provider, item, timeout);
	}

	private boolean checkObjectLifeCycleEventFor(final LoidProvider jdo, final Item item,
			long timeout) {
		boolean result = Runtime4.retry(timeout, new Closure4<Boolean>() {
			public Boolean run() {
				
				final long objectLoid = jdo.loid(item);
				
				ObjectLifecycleEvent objectLifecycleEvent = prototype(ObjectLifecycleEvent.class);
				ObjectSet<ObjectLifecycleEvent> objectLifecycleEvents = _cobra.from(ObjectLifecycleEvent.class).where(objectLifecycleEvent.objectLoid()).equal(objectLoid).select();
				// System.err.println("objectLifecycleEvents.size() " + objectLifecycleEvents.size());
				if(objectLifecycleEvents.size() != 1){
					return false;
				}
				ObjectLifecycleEvent queriedEvent = objectLifecycleEvents.iterator().next();
				Assert.areEqual(Operations.CREATE.value, queriedEvent.operation());
				Assert.isGreater(1, queriedEvent.timestamp());
				Assert.isGreater(1, queriedEvent.classMetadataLoid());
				return true;
			}
		} );
		return result;
	}
	
	public void testIsolationTimeout() throws Exception {
		
		withEventProcessor(new Closure4<Void>() {
			
			public Void run() {
		
				boolean oldState = _provider.pinging();
				_provider.pinging(false);
				
				try {
		
					final EventProcessor ep = _provider.syncEventProcessor();
					
					Assert.isTrue(checkObjectLifeCycleEventFor(_provider, storeAndCommitItem(), 1000));
					
					_provider.runIsolated(new Block4() {
						
						public void run() {
							long timeout = EventProcessorImpl.ISOLATION_TIMEOUT;
							Pair<VodJdoFacade,Item> pair = storeAndCommitItemByOtherUser();
							
							Assert.isFalse(checkObjectLifeCycleEventFor(pair.first, pair.second, timeout/2));
							pair.first.close();
							
							Runtime4.sleepThrowsOnInterrupt(EventProcessorImpl.ISOLATION_TIMEOUT);
							pair = storeAndCommitItemByOtherUser();
							Assert.isTrue(checkObjectLifeCycleEventFor(pair.first, pair.second, timeout/2));
							pair.first.close();
							
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
