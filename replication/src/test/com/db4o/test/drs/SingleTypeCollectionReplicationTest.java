/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.drs;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.replication.db4ounit.DrsTestCase;

import db4ounit.Assert;


public class SingleTypeCollectionReplicationTest extends DrsTestCase {

	protected void actualTest() {
		CollectionHolder h1 = new CollectionHolder();
		h1.map.put("1", "one");
		h1.set.add("two");
		h1.list.add("three");

		a().provider().storeNew(h1);
		a().provider().activate(h1);
		a().provider().commit();

		final ReplicationSession replication = new GenericReplicationSession(a().provider(), b().provider());

		final ObjectSet objectSet = a().provider().objectsChangedSinceLastReplication();

		while (objectSet.hasNext()) {
			replication.replicate(objectSet.next());
		}

		replication.commit();

		ObjectSet it = b().provider().getStoredObjects(CollectionHolder.class);
		Assert.isTrue(it.hasNext());

		CollectionHolder replica = (CollectionHolder) it.next();
		Assert.areEqual("one", replica.map.get("1"));
		Assert.isTrue(replica.set.contains("two"));
		Assert.areEqual("three", replica.list.get(0));
	}

	public void test() {
		actualTest();
	}

}
