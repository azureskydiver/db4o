/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;


import java.util.*;

import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitor.MonitorListener;
import com.db4o.drs.versant.ipc.ObjectLifecycleMonitorNetwork.ClientChannelControl;
import com.db4o.drs.versant.ipc.*;
import com.db4o.drs.versant.metadata.*;
import com.db4o.foundation.*;
import com.versant.odbms.*;
import com.versant.odbms.query.*;
import com.versant.odbms.query.Operator.UnaryOperator;

import db4ounit.*;

public class VodProviderTestCase extends VodProviderTestCaseBase implements TestLifeCycle, ClassLevelFixtureTest {
	
	protected BlockingQueue4<Object> commitedBarrier = new BlockingQueue<Object>();

	public static void main(String[] args) {
		new ConsoleTestRunner(VodProviderTestCase.class).run();
	}
	
	@Override
	public void setUp() {
		super.setUp();
		
		_vod.startEventProcessor();
		
		_provider.syncEventProcessor().addListener(new MonitorListener() {
			
			public void ready() {
			}
			
			public void commited() {
				commitedBarrier.add(new Object());
			}
		});
	}
	
	@Override
	public void tearDown() {
		super.tearDown();
		_vod.stopEventProcessor();
	}
	
	public void testReferenceExists(){
		Item item = storeAndCommitSingleItem();
		ReplicationReference reference = _provider.produceReference(item);
		Assert.isNotNull(reference);
		Assert.areSame(item, reference.object());
	}

	public void testReferenceHasVersion(){
		ReplicationReference reference = _provider.produceReference(storeAndCommitSingleItem());
		Assert.isGreater(0, reference.version());
	}
	
	public void testReferenceIsIdenticalOnMultipleCalls(){
		Item item = storeAndCommitSingleItem();
		ReplicationReference reference1 = _provider.produceReference(item);
		ReplicationReference reference2 = _provider.produceReference(item);
		Assert.areSame(reference1, reference2);
	}
	
	public void testReferenceVersionIsUpdated(){
		Item item = storeAndCommitSingleItem();
		long version1 = _provider.produceReference(item).version();
		update(item);
		
		_provider.clearAllReferences();
		
		ReplicationReference reference2 = _provider.produceReference(item);
		long version2 = reference2.version();
		Assert.isGreater(version1, version2);
	}

	public void testReferenceUUIDIsCreated(){
		Item item = storeAndCommitSingleItem();
		ReplicationReference reference = _provider.produceReference(item);
		DrsUUID uuid = reference.uuid();
		Assert.isNotNull(uuid);
		Assert.isGreater(0, uuid.getSignaturePart().length);
		Assert.areNotEqual(0, uuid.getLongPart());
	}
	
	public void testProduceReferenceByUUID(){
		Item item = storeAndCommitSingleItem();
		ReplicationReference reference = _provider.produceReference(item);
		DrsUUID uuid = reference.uuid();
		Assert.areSame(reference, _provider.produceReferenceByUUID(uuid, null));
	}
	
	public void testReferenceByUUIDReturnsObjectOnClear(){
		Item item = storeAndCommitSingleItem();
		ReplicationReference reference = _provider.produceReference(item);
		DrsUUID uuid = reference.uuid();
		_provider.clearAllReferences();
		Assert.areEqual(item, _provider.produceReferenceByUUID(uuid, null).object());
	}
	
	public void testReferenceByUUIDOnNewProvider(){
		Item item = storeAndCommitSingleItem();
		ReplicationReference reference = _provider.produceReference(item);
		_provider.commit();
		DrsUUID uuid = reference.uuid();
		VodCobraFacade cobra = VodCobra.createInstance(_vod);
		ClientChannelControl comm = ObjectLifecycleMonitorNetworkFactory.newClient(cobra, VodReplicationProvider.class.hashCode());
		VodReplicationProvider provider = new VodReplicationProvider(_vod, cobra, comm);
		Assert.areEqual(item, provider.produceReferenceByUUID(uuid, null).object());
		provider.destroy();
	}
	
	public void testClassMetadataIsLoaded(){
		storeAndCommitSingleItem();
		VodCobraFacade cobra = VodCobra.createInstance(_vod);
		ClientChannelControl comm = ObjectLifecycleMonitorNetworkFactory.newClient(cobra, VodReplicationProvider.class.hashCode());
		VodReplicationProvider secondProvider = new VodReplicationProvider(_vod, cobra, comm);
		storeAndCommitSingleItem(secondProvider);
		secondProvider.destroy();
		assertOnlyOneClassMetadataInstance();
	}

	private void assertOnlyOneClassMetadataInstance() {
		String className = Item.class.getName();
		Collection result = _jdo.query(ClassMetadata.class, "this.fullyQualifiedName == '" + className + "'");
		Assert.areEqual(1, result.size());
		Assert.isTrue(result.contains(new ClassMetadata(null, className)));
	}
	
	public void _testQueryFor_o_ts_timestamp(){
		
		Item item = storeAndCommitSingleItem();
		System.out.println(item);
		ReplicationReference reference = _provider.produceReference(item);
		long version = reference.version();
		System.out.println("Version " + version);
		
		
		DatastoreManager dm = _vod.createDatastoreManager();
		dm.beginTransaction();
		DatastoreQuery query = new DatastoreQuery("Item");
		Expression expression = new Expression(
				new SubExpression(new Field("o_ts_timestamp")), 
				UnaryOperator.GREATER_THAN, 
				new SubExpression(1));
		query.setExpression(expression);
		Object[] loids = dm.executeQuery(query, DataStoreLockMode.NOLOCK,
				DataStoreLockMode.NOLOCK, Options.NO_OPTIONS);
		for (Object loid : loids) {
			System.out.println(loid);
		}
	}

	
	private Item storeAndCommitSingleItem() {
		return storeAndCommitSingleItem(_provider);
	}

	private Item storeAndCommitSingleItem(VodReplicationProvider provider) {
		Item item = new Item("one");
		provider.storeNew(item);
		provider.commit();
		waitForCommitFeedbackFromEventProcessor();
		return item;
	}

	private void waitForCommitFeedbackFromEventProcessor() {
		commitedBarrier.next();
	}
	
	private void update(Item item) {
		item.name("modified");
		_provider.update(item);
		_provider.commit();
		waitForCommitFeedbackFromEventProcessor();
	}
}
