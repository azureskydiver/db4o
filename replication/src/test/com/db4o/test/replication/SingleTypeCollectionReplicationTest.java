/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.replication.ConflictResolver;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;

public abstract class SingleTypeCollectionReplicationTest extends ReplicationTestcase {
	public void testCollectionReplication() {
		init();
		execute();
		cleanUp();
		destroy();
	}

	void execute() {
		CollectionHolder h1 = new CollectionHolder();
		h1._map.put("1", "one");
		h1._set.add("two");
		h1._list.add("three");

		_providerA.storeNew(h1);
		_providerA.activate(h1);

		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB, new ConflictResolver() {
			public Object resolveConflict(ReplicationSession session, Object a, Object b) {
				return null;
			}
		});

		replication.replicate(h1);
		replication.commit();

		ObjectSet it = _providerB.getStoredObjects(CollectionHolder.class);
		Test.ensure(it.hasNext());

		CollectionHolder replica = (CollectionHolder) it.next();
		Test.ensureEquals("one", replica._map.get("1"));
		Test.ensure(replica._set.contains("two"));
		Test.ensureEquals("three", replica._list.get(0));

	}

	protected void cleanUp() {
		delete(new Class[]{CollectionHolder.class});
	}
}
