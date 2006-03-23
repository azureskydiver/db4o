/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.ConflictResolver;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;

public abstract class MixedTypesCollectionReplicationTest extends ReplicationTestcase {
	protected void clean() {
		delete(new Class[]{CollectionHolder.class});
	}

	public void testCollectionReplication() {
		init();

		CollectionHolder h1 = new CollectionHolder("h1");
		CollectionHolder h2 = new CollectionHolder("h2");

		h1._map.put("key", "value");
		h1._map.put("key2", h1);
		h1._map.put(h1, "value2");

		h2._map.put("key", h1);
		h2._map.put(h2, h1);

		h1._list.add("one");
		h1._list.add(h1);

		h2._list.add("two");
		h2._list.add(h1);
		h2._list.add(h2);

		h1._set.add("one");
		h1._set.add(h1);

		h2._set.add("two");
		h2._set.add(h1);
		h2._set.add(h2);


		_providerB.storeNew(h2);
		_providerB.storeNew(h1);

		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB, new ConflictResolver() {
			public Object resolveConflict(ReplicationSession session, Object a, Object b) {
				return null;
			}
		});

		replication.replicate(h2); //Traverses to h1.

		replication.commit();

		ObjectSet objects = _providerA.getStoredObjects(CollectionHolder.class);
		check((CollectionHolder) objects.next(), h1, h2);
		check((CollectionHolder) objects.next(), h1, h2);

		destroy();
	}

	private void check(CollectionHolder holder, CollectionHolder original1, CollectionHolder original2) {
		Test.ensure(holder != original1);
		Test.ensure(holder != original2);

		if (holder._name.equals("h1"))
			checkH1(holder);
		else
			checkH2(holder);
	}

	private void checkH1(CollectionHolder holder) {
		Test.ensureEquals("value", holder._map.get("key"));
		Test.ensureEquals(holder, holder._map.get("key2"));
		Test.ensureEquals("value2", holder._map.get(holder));

		Test.ensureEquals("one", holder._list.get(0));
		Test.ensureEquals(holder, holder._list.get(1));

		Test.ensure(holder._set.contains("one"));
		Test.ensure(holder._set.contains(holder));
	}

	private void checkH2(CollectionHolder holder) {
		Test.ensureEquals("h1", ((CollectionHolder) holder._map.get("key"))._name);
		Test.ensureEquals("h1", ((CollectionHolder) holder._map.get(holder))._name);

		Test.ensureEquals("two", holder._list.get(0));
		Test.ensureEquals("h1", ((CollectionHolder) holder._list.get(1))._name);
		Test.ensureEquals(holder, holder._list.get(2));

		Test.ensure(holder._set.remove("two"));
		Test.ensure(holder._set.remove(holder));
		CollectionHolder remaining = (CollectionHolder) holder._set.iterator().next();
		Test.ensureEquals("h1", remaining._name);
	}

}
