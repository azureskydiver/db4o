/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import javax.jdo.*;



import com.db4o.drs.inside.*;
import com.db4o.drs.test.versant.data.*;

import db4ounit.*;

public class VodReplicationProviderTestCase extends VodDatabaseTestCaseBase implements TestLifeCycle, ClassLevelFixtureTest {
	
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

	public void testReferenceUUID(){
		Item item = storeAndCommitSingleItem();
		ReplicationReference reference = _provider.produceReference(item);
		// Assert.isNotNull(reference.uuid());
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
