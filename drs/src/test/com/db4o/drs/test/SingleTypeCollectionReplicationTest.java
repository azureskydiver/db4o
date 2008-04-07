/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.*;

import com.db4o.drs.db4o.*;
import com.db4o.drs.inside.*;

import db4ounit.*;


public class SingleTypeCollectionReplicationTest extends DrsTestCase {
	
	public void test() {
		CollectionHolder h1 = new CollectionHolder();
		h1.map.put("1", "one");
		h1.set.add("two");
		h1.list.add("three");
		
		storeNewAndCommit(a().provider(), h1);
		
		replicateAll(a().provider(), b().provider());
		
		Iterator it = b().provider().getStoredObjects(CollectionHolder.class).iterator();
		Assert.isTrue(it.hasNext());
		
		CollectionHolder replica = (CollectionHolder) it.next();
		assertSameClassIfDb4o(h1.map, replica.map);
		Assert.areEqual("one", replica.map.get("1"));
		
		assertSameClassIfDb4o(h1.set, replica.set);
		Assert.isTrue(replica.set.contains("two"));
		
		assertSameClassIfDb4o(h1.list, replica.list);
		Assert.areEqual("three", replica.list.get(0));
	}

	private void assertSameClassIfDb4o(final Object expectedInstance,
			final Object actualInstance) {
		if (!isDb4oProvider(a())) return;
		if (!isDb4oProvider(b())) return;
		
		Assert.areSame(expectedInstance.getClass(), actualInstance.getClass());
	}

	private boolean isDb4oProvider(final DrsFixture fixture) {
		return fixture.provider() instanceof Db4oReplicationProvider;
	}
	
	private void storeNewAndCommit(
			final TestableReplicationProviderInside provider,
			CollectionHolder h1) {
		provider.storeNew(h1);
		provider.activate(h1);
		provider.commit();
	}
}
