/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import javax.jdo.*;

import com.db4o.drs.foundation.*;
import com.db4o.drs.inside.*;
import com.db4o.drs.test.versant.data.*;
import com.db4o.drs.versant.*;

import db4ounit.*;

public class VodProviderTestCase extends VodProviderTestCaseBase implements TestLifeCycle, ClassLevelFixtureTest {
	
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
		VodReplicationProvider provider = new VodReplicationProvider(_vod);
		Assert.areEqual(item, provider.produceReferenceByUUID(uuid, null).object());
		provider.destroy();
	}
	
	public void _testQueryForVersion(){
		Item item = storeAndCommitSingleItem();
		ReplicationReference reference = _provider.produceReference(item);
		long version = reference.version();
		System.out.println("Version " + version);
		
		String filter = "this.o_ts_timestamp == param";
		
		_pm.currentTransaction().begin();
		
		Query query = _pm.newQuery(Item.class, filter);
		query.declareParameters("int param");
		Iterable<Item> iterable = (Iterable<Item>) query.execute(version);
		for (Item item2 : iterable) {
			System.out.println("Query worked");
			System.out.println(item2);
		}
		
		_pm.currentTransaction().rollback();
		
		
	}
	
	private Item storeAndCommitSingleItem() {
		Item item = new Item("one");
		_provider.storeNew(item);
		_provider.commit();
		return item;
	}
	
	private void update(Item item) {
		item.name("modified");
		_provider.update(item);
		_provider.commit();
	}
	
}
