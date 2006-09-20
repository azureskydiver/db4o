/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.drs.ReplicationSession;
import com.db4o.drs.inside.GenericReplicationSession;
import com.db4o.test.Test;

public class MixedTypesCollectionReplicationTest extends ReplicationTestCase {
	protected void actualTest() {
		if (!_providerA.supportsHybridCollection()) return;
		if (!_providerB.supportsHybridCollection()) return;

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


		_providerB.storeNew(h2);
		_providerB.storeNew(h1);

		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB);

		replication.replicate(h2); //Traverses to h1.

		replication.commit();

		ObjectSet objects = _providerA.getStoredObjects(CollectionHolder.class);
		check((CollectionHolder) objects.next(), h1, h2);
		check((CollectionHolder) objects.next(), h1, h2);
	}

	private void check(CollectionHolder holder, CollectionHolder original1, CollectionHolder original2) {
		Test.ensure(holder != original1);
		Test.ensure(holder != original2);

		if (holder.name.equals("h1"))
			checkH1(holder);
		else
			checkH2(holder);
	}

	private void checkH1(CollectionHolder holder) {
		Test.ensureEquals("value", holder.map.get("key"));
		Test.ensureEquals(holder, holder.map.get("key2"));
		Test.ensureEquals("value2", holder.map.get(holder));

		Test.ensureEquals("one", holder.list.get(0));
		Test.ensureEquals(holder, holder.list.get(1));

		Test.ensure(holder.set.contains("one"));
		Test.ensure(holder.set.contains(holder));
	}

	private void checkH2(CollectionHolder holder) {
		Test.ensureEquals("h1", ((CollectionHolder) holder.map.get("key")).name);
		Test.ensureEquals("h1", ((CollectionHolder) holder.map.get(holder)).name);

		Test.ensureEquals("two", holder.list.get(0));
		Test.ensureEquals("h1", ((CollectionHolder) holder.list.get(1)).name);
		Test.ensureEquals(holder, holder.list.get(2));

		Test.ensure(holder.set.remove("two"));
		Test.ensure(holder.set.remove(holder));
		CollectionHolder remaining = (CollectionHolder) holder.set.iterator().next();
		Test.ensureEquals("h1", remaining.name);
	}

	public void test() {
		super.test();
	}

}
