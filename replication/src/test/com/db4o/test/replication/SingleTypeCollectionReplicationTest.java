/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.ConflictResolver;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;

public abstract class SingleTypeCollectionReplicationTest {
	protected TestableReplicationProviderInside _containerA;
	protected TestableReplicationProviderInside _containerB;

	public void testCollectionReplication() {
		new SimplestTest().run();
	}

	protected abstract TestableReplicationProviderInside prepareProviderB();

	protected abstract TestableReplicationProviderInside prepareProviderA();

	abstract class TheTest {
		final void run() {
			initProviders();
			execute();
			cleanUp();
		}

		abstract void execute();

		abstract void cleanUp();

		private void initProviders() {
			_containerA = prepareProviderA();
			_containerB = prepareProviderB();
		}
	}

	class SimplestTest extends TheTest {
		void execute() {
			CollectionHolder h1 = new CollectionHolder();
			h1._map.put("1", "one");
			h1._set.add("two");
			h1._list.add("three");

			final ReplicationSession replication = new GenericReplicationSession(_containerA, _containerB, new ConflictResolver() {
				public Object resolveConflict(ReplicationSession session, Object a, Object b) {
					return null;
				}
			});

			_containerA.storeNew(h1);
			_containerA.activate(h1);

			replication.replicate(h1);
			replication.commit();

			ObjectSet it = _containerB.getStoredObjects(CollectionHolder.class);
			Test.ensure(it.hasNext());

			CollectionHolder replica = (CollectionHolder) it.next();
			Test.ensureEquals("one", replica._map.get("1"));
			Test.ensure(replica._set.contains("two"));
			Test.ensureEquals("three", replica._list.get(0));
		}

		void cleanUp() {

		}
	}
}
