/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.drs.test;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.ReplicationSession;
import db4ounit.Assert;

public class MixedTypesCollectionReplicationTest extends DrsTestCase {

	protected void actualTest() {
		if (!a().provider().supportsHybridCollection()) return;
		if (!b().provider().supportsHybridCollection()) return;

		CollectionHolder h1 = new CollectionHolder("h1");
		CollectionHolder h2 = new CollectionHolder("h2");

		h1.map.put("key", "value");
		h1.map.put("key2", h1);
		h1.map.put(h1, "value2");

		h2.map.put("key", h1);
		h2.map.put(h2, h1);

		h1.list.add("one");
		h1.list.add(h1);

		h2.list.add("two");
		h2.list.add(h1);
		h2.list.add(h2);

		h1.set.add("one");
		h1.set.add(h1);

		h2.set.add("two");
		h2.set.add(h1);
		h2.set.add(h2);


		b().provider().storeNew(h2);
		b().provider().storeNew(h1);

		final ReplicationSession replication = new GenericReplicationSession(a().provider(), b().provider());

		replication.replicate(h2); //Traverses to h1.

		replication.commit();

		ObjectSet objects = a().provider().getStoredObjects(CollectionHolder.class);
		check((CollectionHolder) objects.next(), h1, h2);
		check((CollectionHolder) objects.next(), h1, h2);
	}

	private void check(CollectionHolder holder, CollectionHolder original1, CollectionHolder original2) {
		Assert.isTrue(holder != original1);
		Assert.isTrue(holder != original2);

		if (holder.name.equals("h1"))
			checkH1(holder);
		else
			checkH2(holder);
	}

	private void checkH1(CollectionHolder holder) {
		Assert.areEqual("value", holder.map.get("key"));
		Assert.areEqual(holder, holder.map.get("key2"));
		Assert.areEqual("value2", holder.map.get(holder));

		Assert.areEqual("one", holder.list.get(0));
		Assert.areEqual(holder, holder.list.get(1));

		Assert.isTrue(holder.set.contains("one"));
		Assert.isTrue(holder.set.contains(holder));
	}

	private void checkH2(CollectionHolder holder) {
		Assert.areEqual("h1", ((CollectionHolder) holder.map.get("key")).name);
		Assert.areEqual("h1", ((CollectionHolder) holder.map.get(holder)).name);

		Assert.areEqual("two", holder.list.get(0));
		Assert.areEqual("h1", ((CollectionHolder) holder.list.get(1)).name);
		Assert.areEqual(holder, holder.list.get(2));

		Assert.isTrue(holder.set.remove("two"));
		Assert.isTrue(holder.set.remove(holder));
		CollectionHolder remaining = (CollectionHolder) holder.set.iterator().next();
		Assert.areEqual("h1", remaining.name);
	}

	public void test() {
		actualTest();
	}

}
