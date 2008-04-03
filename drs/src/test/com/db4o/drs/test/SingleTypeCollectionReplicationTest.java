/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import java.util.*;

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
		assertSameClass(h1.map, replica.map);
		Assert.areEqual("one", replica.map.get("1"));
		
		assertSameClass(h1.set, replica.set);
		Assert.isTrue(replica.set.contains("two"));
		
		assertSameClass(h1.list, replica.list);
		Assert.areEqual("three", replica.list.get(0));
	}

	private void assertSameClass(final Object expectedInstance,
			final Object actualInstance) {
		Assert.areSame(expectedInstance.getClass(), actualInstance.getClass());
	}
	
	private void storeNewAndCommit(
			final TestableReplicationProviderInside provider,
			CollectionHolder h1) {
		provider.storeNew(h1);
		provider.activate(h1);
		provider.commit();
	}
}
